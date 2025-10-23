package greencity.service;

import static greencity.constant.ErrorMessage.INVALID_TAGS_WEIGHTS;
import com.github.benmanes.caffeine.cache.Cache;
import greencity.entity.EcoNews;
import greencity.entity.Habit;
import greencity.utils.RelevanceWeightUtils;
import greencity.dto.cache.CachedRelevancePools;
import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.dto.cache.CachedUserRelevantNews;
import greencity.dto.cache.RelevantEcoNewsCacheKey;
import greencity.dto.relevance.EcoNewsWithRelevanceVectorsDto;
import greencity.dto.relevance.EventWithTagsVectorDto;
import greencity.dto.relevance.HabitWithTagsVectorDto;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.HabitAssign;
import greencity.entity.Tag;
import greencity.entity.TagsCoherence;
import greencity.entity.event.Event;
import greencity.enums.TagType;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EventRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.TagsCoherenceRepo;
import greencity.repository.TagsRepo;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {
    private final EcoNewsRepo ecoNewsRepo;
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;
    private final TagsRepo tagsRepo;
    private final HabitAssignRepo habitAssignRepo;
    private final EventRepo eventRepo;
    private final TagsCoherenceRepo tagsCoherenceRepo;
    private final Cache<RelevantEcoNewsCacheKey, CachedUserRelevantNews> userRelevanceNewsCache;
    private final Cache<Long, CachedUserRelevanceProfile> userProfileCache;
    private final Cache<Long, CachedTagsWithCoherence> tagsCoherenceCache;

    @Value("${greencity.econews.habits.events.tags.weights}")
    private String tagsWeightsString;
    private double[] tagsWeights;

    /**
     * Converts tag weights parameter to an array of doubles and checks if the
     * length of the array is 3. If not, application won't run.
     */
    @PostConstruct
    public void init() {
        this.tagsWeights = RelevanceWeightUtils.parseAndValidateRatios(tagsWeightsString,
            3, INVALID_TAGS_WEIGHTS, true, true);
    }

    /**
     * Get cached relevant news saved for specific user by request filters. If there
     * is no saved data in cache, it will be created and saved.
     *
     * @param key request filters and user specification
     * @return cached news identifiers and metadata
     */
    @Override
    public CachedUserRelevantNews getUserRelevantNewsFromCache(RelevantEcoNewsCacheKey key) {
        CachedUserRelevantNews cachedUserRelevantNews = userRelevanceNewsCache.getIfPresent(key);
        if (cachedUserRelevantNews == null) {
            long totalEcoNewsCount = ecoNewsRepo.count();
            ZoneId zoneId = ZoneId.systemDefault();
            cachedUserRelevantNews = new CachedUserRelevantNews(
                new CachedRelevancePools(new LinkedList<>(), new LinkedList<>(), new LinkedList<>()),
                new HashMap<>(),
                -1,
                (int) Math.ceil((double) totalEcoNewsCount / key.pageSize()),
                totalEcoNewsCount,
                LocalDate.now().plusDays(1).atStartOfDay(zoneId));
            userRelevanceNewsCache.put(key, cachedUserRelevantNews);
        }
        return cachedUserRelevantNews;
    }

    /**
     * Get cached user profile saved for specific user. If there is no saved data in
     * cache, it will be created and saved.
     *
     * @param userId user id
     * @return an instance of {@link CachedUserRelevanceProfile} containing eco news
     *         tags and title vectors as user's relevance preferences
     */
    @Override
    public CachedUserRelevanceProfile getUserProfileFromCache(Long userId) {
        CachedUserRelevanceProfile userProfile = userProfileCache.getIfPresent(userId);
        if (userProfile == null) {
            CachedTagsWithCoherence tags = getTagsCoherenceFromCache();

            List<EcoNewsRelevance> lastLikedNews = ecoNewsRelevanceRepo.findLikedEcoNewsByUserId(userId);
            List<EcoNewsWithRelevanceVectorsDto> ecoNewsWithRelevance = lastLikedNews.stream()
                .map(relevance -> {
                    EcoNews ecoNews = relevance.getEcoNews();
                    List<Long> tagIds = Optional.ofNullable(ecoNews.getTags())
                        .orElse(Collections.emptyList()).stream()
                        .map(Tag::getId)
                        .toList();
                    Float[] titleVector = null;
                    if (!relevance.getIsOutdated()) {
                        titleVector = relevance.getTitleVector();
                    }
                    return new EcoNewsWithRelevanceVectorsDto(ecoNews.getId(), tagIds, titleVector,
                        tags.ecoNewsTagsIndexes());
                })
                .toList();

            Float[] averageTagsVector = mergeUserTagsVector(userId, ecoNewsWithRelevance, tags);

            List<Float[]> titleVectors = ecoNewsWithRelevance.stream()
                .map(EcoNewsWithRelevanceVectorsDto::getTitleVector)
                .filter(Objects::nonNull)
                .toList();

            Float[] averageTitleVector = averageVector(titleVectors);
            userProfile = new CachedUserRelevanceProfile(averageTagsVector, averageTitleVector);
            userProfileCache.put(userId, userProfile);
        }
        return userProfile;
    }

    /**
     * Get cached tags and tags coherence data. If the data is not already cached,
     * it fetches all the tags and tags coherence from the repository, constructs
     * the coherence matrix, and caches the result.
     *
     * @return an instance of {@link CachedTagsWithCoherence} containing indexes for
     *         eco news, event, habit tags, and their coherence matrix.
     */
    @Override
    public CachedTagsWithCoherence getTagsCoherenceFromCache() {
        CachedTagsWithCoherence cachedTags = tagsCoherenceCache.getIfPresent(0L);
        if (cachedTags == null) {
            List<Tag> allTags = tagsRepo.findAll();
            Map<Long, Integer> ecoNewsTagsIndexes = new HashMap<>();
            Map<Long, Integer> eventTagsIndexes = new HashMap<>();
            Map<Long, Integer> habitTagsIndexes = new HashMap<>();

            allTags.forEach(tag -> {
                Map<Long, Integer> targetMap;
                if (tag.getType() == TagType.ECO_NEWS) {
                    targetMap = ecoNewsTagsIndexes;
                } else if (tag.getType() == TagType.EVENT) {
                    targetMap = eventTagsIndexes;
                } else if (tag.getType() == TagType.HABIT) {
                    targetMap = habitTagsIndexes;
                } else {
                    return;
                }
                targetMap.put(tag.getId(), targetMap.size());
            });

            Map<Long, Map<Long, Float>> coherenceMatrix = tagsCoherenceRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                    tc -> tc.getSourceTag().getId(),
                    Collectors.toMap(
                        tc -> tc.getDestinationTag().getId(),
                        TagsCoherence::getCoherence)));
            cachedTags = new CachedTagsWithCoherence(ecoNewsTagsIndexes, eventTagsIndexes,
                habitTagsIndexes, coherenceMatrix);
            tagsCoherenceCache.put(0L, cachedTags);
        }
        return cachedTags;
    }

    /**
     * Find liked eco news and events by user, and assigned habits, get their tags
     * and map them to single tags vector, that represents eco news preference by
     * tags.
     */
    private Float[] mergeUserTagsVector(Long userId,
        List<EcoNewsWithRelevanceVectorsDto> ecoNews,
        CachedTagsWithCoherence tags) {
        Map<Long, Map<Long, Float>> coherenceMatrix = tags.tagsCoherenceIds();
        Map<Long, Integer> ecoNewsTagsIndexes = tags.ecoNewsTagsIndexes();
        int vectorLength = ecoNewsTagsIndexes.size();

        Float[] ecoNewsTagsVector = getEcoNewsVector(ecoNews, vectorLength);
        Float[] habitTagsVector = getHabitTagsAsEcoNewsVector(userId, ecoNewsTagsIndexes,
            tags.habitTagsIndexes(), coherenceMatrix);
        Float[] eventTagsVector = getEventTagsAsEcoNewsVector(userId, ecoNewsTagsIndexes,
            tags.eventTagsIndexes(), coherenceMatrix);

        Float[][] vectors = new Float[][] {ecoNewsTagsVector, habitTagsVector, eventTagsVector};
        double[] normalizedWeights = rearrangeTagsWeights(tagsWeights, vectors, vectorLength);
        if (normalizedWeights[0] == 1
            && Arrays.stream(ecoNewsTagsVector).allMatch(w -> w == 0.0)) {
            return new Float[0];
        }

        return mergeTagsVectorByWeight(normalizedWeights, vectors, vectorLength);
    }

    private Float[] getEcoNewsVector(List<EcoNewsWithRelevanceVectorsDto> ecoNews,
        int requiredVectorLength) {
        if (ecoNews.isEmpty()) {
            Float[] noPreferenceVector = new Float[requiredVectorLength];
            Arrays.fill(noPreferenceVector, 0.0f);
            return noPreferenceVector;
        }

        List<Float[]> ecoNewsTagsVectors = ecoNews.stream()
            .map(EcoNewsWithRelevanceVectorsDto::getTagsVector)
            .toList();
        return averageVector(ecoNewsTagsVectors);
    }

    private Float[] getHabitTagsAsEcoNewsVector(Long userId,
        Map<Long, Integer> ecoNewsTagsIndexes,
        Map<Long, Integer> habitTagsIndexes,
        Map<Long, Map<Long, Float>> coherenceMatrix) {
        List<HabitAssign> lastActiveHabits = habitAssignRepo.findAllByUserId(userId);
        List<Float[]> habitAssignTagsVectors = lastActiveHabits.stream()
            .filter(habitAssign -> habitAssign.getHabit().getTags() != null
                && !habitAssign.getHabit().getTags().isEmpty())
            .map(habitAssign -> {
                Habit habit = habitAssign.getHabit();
                List<Long> tagIds = Optional.of(habit.getTags())
                    .orElse(Collections.emptySet()).stream()
                    .map(Tag::getId)
                    .toList();
                return new HabitWithTagsVectorDto(habit.getId(), tagIds, getTagsCoherenceFromCache());
            })
            .map(HabitWithTagsVectorDto::getTagsVector)
            .toList();

        Float[] averageHabitTagsVector = averageVector(habitAssignTagsVectors);
        return convertTagsToEcoNewsTagsVector(averageHabitTagsVector, ecoNewsTagsIndexes,
            habitTagsIndexes, coherenceMatrix);
    }

    private Float[] getEventTagsAsEcoNewsVector(Long userId,
        Map<Long, Integer> ecoNewsTagsIndexes,
        Map<Long, Integer> eventTagsIndexes,
        Map<Long, Map<Long, Float>> coherenceMatrix) {
        List<Event> lastLikedEvents = eventRepo.findLikedEventsByUserId(userId);
        List<Float[]> likedEventsTagsVectors = lastLikedEvents.stream()
            .filter(event -> event.getTags() != null && !event.getTags().isEmpty())
            .map(event -> {
                List<Long> tagIds = Optional.of(event.getTags())
                    .orElse(Collections.emptyList()).stream()
                    .map(Tag::getId)
                    .toList();
                return new EventWithTagsVectorDto(event.getId(), tagIds, getTagsCoherenceFromCache());
            })
            .map(EventWithTagsVectorDto::getTagsVector)
            .toList();

        Float[] averageEventTagsVector = averageVector(likedEventsTagsVectors);
        return convertTagsToEcoNewsTagsVector(averageEventTagsVector, ecoNewsTagsIndexes,
            eventTagsIndexes, coherenceMatrix);
    }

    /**
     * Convert not eco news tags vector to eco news tags vector using tags coherence
     * matrix.
     */
    private Float[] convertTagsToEcoNewsTagsVector(Float[] otherTagsVector,
        Map<Long, Integer> ecoNewsTagsIndexes,
        Map<Long, Integer> otherTagsIndexes,
        Map<Long, Map<Long, Float>> coherenceMatrix) {
        if (otherTagsVector.length == 0) {
            return new Float[0];
        }

        Float[] ecoNewsTagsProjection = new Float[ecoNewsTagsIndexes.size()];
        Arrays.fill(ecoNewsTagsProjection, 0.0f);

        ecoNewsTagsIndexes
            .forEach((ecoNewsTagId, ecoNewsTagIndex) -> otherTagsIndexes
                .forEach((otherTagId, otherTagIndex) -> {
                    Map<Long, Float> destinations = coherenceMatrix.getOrDefault(otherTagId, Map.of());
                    Float coherence = destinations.getOrDefault(ecoNewsTagId, 0.0f);
                    ecoNewsTagsProjection[ecoNewsTagIndex] += coherence * otherTagsVector[otherTagIndex];
                }));

        return ecoNewsTagsProjection;
    }

    private double[] rearrangeTagsWeights(double[] tagsWeights, Float[][] tagsVectors, int requiredVectorLength) {
        double[] weights = new double[3];
        for (int i = 0; i < 3; i++) {
            if (tagsVectors[i].length == requiredVectorLength) {
                weights[i] = tagsWeights[i];
            } else {
                weights[i] = 0.0;
            }
        }

        return RelevanceWeightUtils.normalizeWeights(weights);
    }

    private Float[] mergeTagsVectorByWeight(double[] normalizedWeights, Float[][] vectors, int requiredVectorLength) {
        Float[] merged = new Float[requiredVectorLength];
        Arrays.fill(merged, 0f);

        for (int i = 0; i < 3; i++) {
            float weight = (float) normalizedWeights[i];

            if (weight == 0.0
                || vectors[i].length != requiredVectorLength) {
                continue;
            }

            for (int j = 0; j < requiredVectorLength; j++) {
                merged[j] += vectors[i][j] * weight;
            }
        }

        return merged;
    }

    private Float[] averageVector(List<Float[]> vectors) {
        if (vectors.isEmpty()) {
            return new Float[0];
        }

        int dimension = vectors.stream()
            .filter(vector -> vector != null && vector.length != 0)
            .map(vector -> vector.length)
            .collect(Collectors.groupingBy(
                length -> length,
                Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(0);

        return IntStream.range(0, dimension)
            .mapToDouble(i -> vectors.stream()
                .filter(vector -> vector != null && vector.length == dimension)
                .mapToDouble(vector -> vector[i])
                .average()
                .orElse(0.0))
            .mapToObj(vectorValue -> (float) vectorValue)
            .toArray(Float[]::new);
    }
}

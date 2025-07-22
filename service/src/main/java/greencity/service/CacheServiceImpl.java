package greencity.service;

import com.github.benmanes.caffeine.cache.Cache;
import greencity.converters.RatioConverter;
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
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanInitializationException;
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

    @PostConstruct
    public void init() {
        this.tagsWeights = RatioConverter.convertRatioFromString(tagsWeightsString);
        if (tagsWeights.length != 3) {
            throw new BeanInitializationException(String.format("Invalid tags weights parameter value. "
                + "Expected 3 values, but got %d.", tagsWeights.length));
        }
    }

    @Override
    public CachedUserRelevantNews getUserRelevantNewsFromCache(RelevantEcoNewsCacheKey key) {
        CachedUserRelevantNews cachedUserRelevantNews = userRelevanceNewsCache.getIfPresent(key);
        if (cachedUserRelevantNews == null) {
            long totalEcoNewsCount = ecoNewsRepo.count();
            cachedUserRelevantNews = new CachedUserRelevantNews(
                new CachedRelevancePools(new LinkedList<>(), new LinkedList<>(), new LinkedList<>()),
                new HashMap<>(),
                0,
                (int) (totalEcoNewsCount / key.pageSize()),
                ZonedDateTime.now().plusDays(1)
            );
            userRelevanceNewsCache.put(key, cachedUserRelevantNews);
        }
        return cachedUserRelevantNews;
    }
    ///  have to add checking for vectors, if any is null -> give all priority to another one
    @Override
    public CachedUserRelevanceProfile getUserProfileFromCache(Long userId) {
        CachedUserRelevanceProfile userProfile = userProfileCache.getIfPresent(userId);
        if (userProfile == null) {
            CachedTagsWithCoherence tags = getTagsCoherenceFromCacheForUser();

            List<EcoNewsRelevance> lastLikedNews = ecoNewsRelevanceRepo.findLikedEcoNewsByUserId(userId);
            List<EcoNewsWithRelevanceVectorsDto> ecoNewsWithRelevance = lastLikedNews.stream()
                    .map(relevance ->
                            new EcoNewsWithRelevanceVectorsDto(relevance.getEcoNews(), relevance, tags.ecoNewsTagsIndexes()))
                    .toList();

            Float[] averageTagsVector = mergeUserTagsVector(userId, ecoNewsWithRelevance, tags);

            List<Float[]> titleVectors = ecoNewsWithRelevance.stream()
                    .map(EcoNewsWithRelevanceVectorsDto::getTitleVector)
                    .filter(Objects::nonNull)
                    .toList();

            Float[] averageTitleVector = titleVectors.isEmpty()
                    ? new Float[0]
                    : averageVector(titleVectors);

            userProfile = new CachedUserRelevanceProfile(averageTagsVector, averageTitleVector);
            userProfileCache.put(userId, userProfile);
        }
        return userProfile;
    }


    @Override
    public CachedTagsWithCoherence getTagsCoherenceFromCacheForUser() {
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
                            TagsCoherence::getCoherence)
                    )
                );
            cachedTags = new CachedTagsWithCoherence(ecoNewsTagsIndexes, eventTagsIndexes,
                habitTagsIndexes, coherenceMatrix);
            tagsCoherenceCache.put(0L, cachedTags);
        }
        return cachedTags;
    }
    private Float[] mergeUserTagsVector(Long userId,
                                        List<EcoNewsWithRelevanceVectorsDto> ecoNews,
                                        CachedTagsWithCoherence tags) {
        Map<Long, Map<Long, Float>> coherenceMatrix = tags.tagsCoherenceIds();
        Map<Long, Integer> ecoNewsTagsIndexes = tags.ecoNewsTagsIndexes();
        int vectorLength = ecoNewsTagsIndexes.size();

        Float[] ecoNewsTagsVector = getEcoNewsVector(ecoNews, ecoNewsTagsIndexes);
        Float[] habitTagsVector = getHabitTagsAsEcoNewsVector(userId, ecoNewsTagsIndexes,
                tags.habitTagsIndexes(), coherenceMatrix);
        Float[] eventTagsVector = getEventTagsAsEcoNewsVector(userId, ecoNewsTagsIndexes,
                tags.eventTagsIndexes(), coherenceMatrix);

        Float[][] vectors = new Float[][]{ecoNewsTagsVector, habitTagsVector, eventTagsVector};
        double[] weights = new double[3];
        double totalWeight = 0.0;

        for (int i = 0; i < 3; i++) {
            if (vectors[i] != null && vectors[i].length == vectorLength) {
                weights[i] = tagsWeights[i];
                totalWeight += tagsWeights[i];
            } else {
                weights[i] = 0.0;
            }
        }

        if (totalWeight == 0.0) {
            return new Float[vectorLength];
        }

        Float[] merged = new Float[vectorLength];
        Arrays.fill(merged, 0f);

        for (int i = 0; i < 3; i++) {
            if (weights[i] == 0.0) continue;
            Float[] vec = vectors[i];
            float weight = (float)(weights[i] / totalWeight);
            for (int j = 0; j < vectorLength; j++) {
                Float value = vec[j];
                if (value != null) {
                    merged[j] += value * weight;
                }
            }
        }

        return normalizedVector(merged);
    }



    private Float[] getEcoNewsVector(List<EcoNewsWithRelevanceVectorsDto> ecoNews,
                                     Map<Long, Integer> ecoNewsTagsIndexes) {
        List<Float[]> ecoNewsTagsVectors = ecoNews.stream()
            .map(EcoNewsWithRelevanceVectorsDto::getTagsVector)
            .toList();
        if (ecoNewsTagsVectors.isEmpty()) {
            return new Float[0];
        }

        Float[] averageEcoNewsTagsVector = averageVector(ecoNewsTagsVectors);
        return normalizedVector(averageEcoNewsTagsVector);
    }

    private Float[] getHabitTagsAsEcoNewsVector(Long userId,
                                                Map<Long, Integer> ecoNewsTagsIndexes,
                                                Map<Long, Integer> habitTagsIndexes,
                                                Map<Long, Map<Long, Float>> coherenceMatrix) {
        List<HabitAssign> lastActiveHabits = habitAssignRepo.findAllByUserId(userId);
        List<Float[]> habitAssignTagsVectors = lastActiveHabits.stream()
            .map(habitAssign ->
                new HabitWithTagsVectorDto(habitAssign.getHabit(), getTagsCoherenceFromCacheForUser()))
            .map(HabitWithTagsVectorDto::getTagsVector)
            .toList();
        if (habitAssignTagsVectors.isEmpty()) {
            return new Float[0];
        }
        
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
            .map(event -> new EventWithTagsVectorDto(event, getTagsCoherenceFromCacheForUser()))
            .map(EventWithTagsVectorDto::getTagsVector)
            .toList();
        if (likedEventsTagsVectors.isEmpty()) {
            return new Float[0];
        }

        Float[] averageEventTagsVector = averageVector(likedEventsTagsVectors);
        return convertTagsToEcoNewsTagsVector(averageEventTagsVector, ecoNewsTagsIndexes,
            eventTagsIndexes, coherenceMatrix);
    }

    private Float[] convertTagsToEcoNewsTagsVector(Float[] otherTagsVector,
                                                  Map<Long, Integer> ecoNewsTagsIndexes,
                                                  Map<Long, Integer> otherTagsIndexes,
                                                  Map<Long, Map<Long, Float>> coherenceMatrix) {
        Float[] ecoNewsTagsProjection = new Float[ecoNewsTagsIndexes.size()];

        ecoNewsTagsIndexes.forEach((ecoNewsTagId, ecoNewsTagIndex) -> 
            otherTagsIndexes.forEach((otherTagId, otherTagIndex) -> {
                Map<Long, Float> destinations = coherenceMatrix.getOrDefault(otherTagId, Map.of());
                Float weight = destinations.getOrDefault(ecoNewsTagId, 0.0f);
                ecoNewsTagsProjection[ecoNewsTagIndex] += weight * otherTagsVector[otherTagIndex];
            })
        );
        
        return normalizedVector(ecoNewsTagsProjection);
    }

    private Float[] averageVector(List<Float[]> vectors) {
        int dimension = vectors.get(0).length;
        Float[] averageVector = new Float[dimension];
        Arrays.fill(averageVector, 0f);

        for (int i = 0; i < dimension; i++) {
            for (Float[] vector : vectors) {
                averageVector[i] += vector[i];
            }
            averageVector[i] /= vectors.size();
        }

        return averageVector;
    }

    private Float[] normalizedVector(Float[] vector) {
        Float[] normalizedVector = new Float[vector.length];
        Arrays.fill(normalizedVector, 0f);

        double sum = Arrays.stream(vector)
            .mapToDouble(Float::doubleValue)
            .sum();
        for (int i = 0; i < vector.length; i++) {
            vector[i] = (float) (vector[i] / sum);
        }

        return normalizedVector;
    }
}

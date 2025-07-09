package greencity.service;

import com.github.benmanes.caffeine.cache.Cache;
import greencity.cache.CachedRelevancePools;
import greencity.cache.CachedUserProfile;
import greencity.cache.CachedUserRelevantNews;
import greencity.cache.RelevantEcoNewsCacheKey;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsDto;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.enums.TagType;
import greencity.filters.EcoNewsSpecification;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.Tag;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.TagsCoherenceRepo;
import greencity.repository.TagsRepo;
import jakarta.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EcoNewsRelevanceServiceImpl implements EcoNewsRelevanceService {
    private static double[] RELEVANCE_POOLS_RATIO;
    private static double[] RELEVANCE_SCORES_WEIGHTS;

    private final EcoNewsRepo ecoNewsRepo;
    private final EcoNewsServiceImpl ecoNewsService;
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;
    private final TagsRepo tagsRepo;
    private final TagsCoherenceRepo tagsCoherenceRepo;
    private final Cache<RelevantEcoNewsCacheKey, CachedUserRelevantNews> userRelevanceNewsCache;
    private final Cache<Long, CachedUserProfile> userProfileCache;
    private final ModelMapper modelMapper;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostConstruct
    public void init(@Value("${greencity.relevant.news.ratio}") String relevancePoolsRatio,
                     @Value("${greencity.tags.semantic.scores.weights}") String semanticScoresWeights) {
        RELEVANCE_POOLS_RATIO = convertRatioFromString(relevancePoolsRatio);
        RELEVANCE_SCORES_WEIGHTS = convertRatioFromString(semanticScoresWeights);
    }

    private double[] convertRatioFromString(String ratio) {
        double[] ratioDoubles = Arrays.stream(ratio.split(":"))
            .mapToDouble(Double::parseDouble)
            .toArray();
        double sum = Arrays.stream(ratioDoubles).sum();
        return sum <= 1
            ? ratioDoubles
            : Arrays.stream(ratioDoubles)
            .map(d -> d / sum)
            .toArray();
    }

    @Override
    @Transactional
    public PageableDto<EcoNewsDto> findRelevantEcoNews(Pageable pageable,
                                                       List<String> tags,
                                                       String title,
                                                       String author,
                                                       UserVO user) {
        RelevantEcoNewsCacheKey key = new RelevantEcoNewsCacheKey(
            user.getId(),
            String.join(",", tags),
            title,
            author,
            pageable.getPageSize());
        CachedUserRelevantNews cachedUserRelevantNews = userRelevanceNewsCache.getIfPresent(key);
        if (cachedUserRelevantNews == null) {
            long totalEcoNewsCount = ecoNewsRepo.count();
            cachedUserRelevantNews = new CachedUserRelevantNews(
                new CachedRelevancePools(new LinkedList<>(), new LinkedList<>(), new LinkedList<>()),
                new HashMap<>(),
                (int) (totalEcoNewsCount / pageable.getPageSize()),
                ZonedDateTime.now()
            );
        }

        CachedRelevancePools pools = cachedUserRelevantNews.newsRelevancePools();
        Map<Integer, List<Long>> relevantNewsPages = cachedUserRelevantNews.relevantNewsPages();
        int page = pageable.getPageNumber();
        List<EcoNews> findResult;

        if (relevantNewsPages.containsKey(page)) {
            findResult = ecoNewsRepo.findAllById(relevantNewsPages.get(page));
        } else {
            findResult = new ArrayList<>();

        }

        return null;
    }

    private List<EcoNews> getFilteredNews(RelevantEcoNewsCacheKey requestMetadata, ZonedDateTime lastRequestedDate) {
        //todo check date correctness
        EcoNewsViewDto request = EcoNewsViewDto.builder()
            .title(requestMetadata.title())
            .author(requestMetadata.author())
            .tags(requestMetadata.tags())
            .startDate(lastRequestedDate.minusWeeks(1).format(formatter))
            .endDate(lastRequestedDate.format(formatter))
            .build();
        EcoNewsSpecification specification = ecoNewsService.getSpecification(request);
        return ecoNewsRepo.findAll(specification, Sort.by(Sort.Direction.DESC, "creationDate"));
    }

//    private void fillPools(UserVO user, List<EcoNews> ecoNews) {
//        ecoNews.stream().forEach(news -> {
//
//        })
//    }
//
//    private List<EcoNews> getResultByRatio(CachedRelevancePools pools, int pageSize) {
////        double[] normalizedRatio = convertRatioFromString(relevancePoolsRatio);
//        int[] ratioForPages = Arrays.stream(RELEVANCE_POOLS_RATIO)
//            .mapToInt(d -> (int) Math.round(d * pageSize))
//            .toArray();
//    }
//
//    private void loadMoreNews(LinkedList<EcoNews> pool, int neededCount) {
//        if (pool.size() < neededCount) {
//
//        }
//    }

    private float countRelevanceForUserAndEcoNews(UserVO user, EcoNewsRelevance ecoNewsRelevance) {
        return 0;
    }

    private CachedUserProfile getAverageVectorsByUser(Long userId) {
        CachedUserProfile cachedUserProfile = userProfileCache.getIfPresent(userId);
        if (cachedUserProfile != null) {
            return cachedUserProfile;
        }

        List<EcoNewsRelevance> lastLikedNews = ecoNewsRelevanceRepo.findLikedEcoNewsById(userId);
        List<EcoNewsWithRelevanceVectors> likedNewsWithVectors = lastLikedNews.stream()
            .map(relevance -> new EcoNewsWithRelevanceVectors(relevance.getEcoNews(), relevance))
            .toList();

        Float[] averageTitleVector = averageVectors(likedNewsWithVectors.stream()
            .map(EcoNewsWithRelevanceVectors::getTitleVector)
            .toList());
        Float[] averageTagsVector = averageVectors(likedNewsWithVectors.stream()
            .map(EcoNewsWithRelevanceVectors::getTagsVector)
            .toList());
        cachedUserProfile = new CachedUserProfile(averageTagsVector, averageTitleVector);
        userProfileCache.put(userId, cachedUserProfile);
        return cachedUserProfile;
    }

    private Float[] averageVectors(List<Float[]> vectors) {
        if (vectors == null || vectors.isEmpty()) {
            return null;
        }
        int dimension = vectors.get(0).length;
        Float[] averageVector = new Float[dimension];
        Arrays.fill(averageVector, 0f);

        for (Float[] vector : vectors) {
            for (int i = 0; i < dimension; i++) {
                averageVector[i] += vector[i];
            }
        }
        for (int i = 0; i < dimension; i++) {
            averageVector[i] /= dimension;
        }
        return averageVector;
    }

    private record RelevancePools(
        LinkedList<EcoNews> relevantStrongNews,
        LinkedList<EcoNews> relevantWeakNews,
        LinkedList<EcoNews> nonRelevantNews
    ) {
    }

    @Getter
    private class EcoNewsWithRelevanceVectors {
        private final EcoNews ecoNews;
        private final Float[] titleVector;
        private final Float[] tagsVector;

        //static
        private final List<Tag> ecoNewsTags = tagsRepo.findTagsByType(TagType.ECO_NEWS);

        public EcoNewsWithRelevanceVectors(EcoNews ecoNews, EcoNewsRelevance ecoNewsRelevance) {
            this.ecoNews = ecoNews;
            this.titleVector = ecoNewsRelevance.getTitleVector();
            this.tagsVector = new Float[ecoNewsTags.size()];
            ecoNewsTags.forEach(tag -> tagsVector[ecoNewsTags.indexOf(tag)] = 1f);
        }

        public double calculateRelevanceScore(CachedUserProfile userProfile) {
            double tagsRelevance = RELEVANCE_SCORES_WEIGHTS[0]
                * getCosineSimilarity(tagsVector, userProfile.tagsPreferencesVector());
            double titleRelevance = RELEVANCE_SCORES_WEIGHTS[1]
                * getCosineSimilarity(titleVector, userProfile.titlePreferencesVector());
            return tagsRelevance + titleRelevance;
        }

        private double getCosineSimilarity(Float[] vector1, Float[] vector2) {
            if (vector1 == null || vector2 == null || vector1.length != vector2.length) {
                return 0.0;
            }
            double dot = 0.0;
            double normA = 0.0;
            double normB = 0.0;
            for (int i = 0; i < vector1.length; i++) {
                dot += vector1[i] * vector2[i];
                normA += vector1[i] * vector1[i];
                normB += vector2[i] * vector2[i];
            }
            if (normA == 0 && normB == 0) {
                return 0.0;
            }
            return dot / (Math.sqrt(normA) * Math.sqrt(normB));
        }
    }
}

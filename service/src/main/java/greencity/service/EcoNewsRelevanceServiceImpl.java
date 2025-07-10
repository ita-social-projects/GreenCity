package greencity.service;

import com.github.benmanes.caffeine.cache.Cache;
import greencity.cache.CachedRelevancePools;
import greencity.cache.CachedUserProfile;
import greencity.cache.CachedUserRelevantNews;
import greencity.cache.RelevantEcoNewsCacheKey;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.enums.TagType;
import greencity.filters.EcoNewsSpecification;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.Tag;
import greencity.mapping.EcoNewsGenericDtoMapper;
import greencity.mapping.PageableAdvancedDtoMapper;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.TagsCoherenceRepo;
import greencity.repository.TagsRepo;
import jakarta.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final EcoNewsRepo ecoNewsRepo;
    private final EcoNewsServiceImpl ecoNewsService;
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;
    private final TagsRepo tagsRepo;
    private final TagsCoherenceRepo tagsCoherenceRepo;
    private final Cache<RelevantEcoNewsCacheKey, CachedUserRelevantNews> userRelevanceNewsCache;
    private final Cache<Long, CachedUserProfile> userProfileCache;
    private final ModelMapper modelMapper;
    private final PageableAdvancedDtoMapper<EcoNewsGenericDto> pageableAdvancedDtoMapper;
    private final EcoNewsGenericDtoMapper ecoNewsGenericDtoMapper;

    private final List<Tag> ecoNewsTags = tagsRepo.findTagsByType(TagType.ECO_NEWS);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private double[] relevancePoolsRatio;
    private double[] relevanceScoresWeights;
    private double[] relevanceScoresStrength;

    @PostConstruct
    public void init(@Value("${greencity.relevant.news.ratio}") String relevancePoolsRatio,
                     @Value("${greencity.tags.semantic.scores.weights}") String semanticScoresWeights,
                     @Value("${greencity.relevance.scores.strengths}") String relevanceScoresStrength) {
        this.relevancePoolsRatio = convertRatioFromString(relevancePoolsRatio);
        this.relevanceScoresWeights = convertRatioFromString(semanticScoresWeights);
        this.relevanceScoresStrength = convertRatioFromString(relevanceScoresStrength);
    }

    private double[] convertRatioFromString(String ratio) {
        double[] ratioDoubles = Arrays.stream(ratio.split(":"))
            .mapToDouble(Double::parseDouble)
            .toArray();
        double sum = Arrays.stream(ratioDoubles).sum();
        return sum <= 1 ? ratioDoubles : Arrays.stream(ratioDoubles)
            .map(d -> d / sum)
            .toArray();
    }

    @Override
    @Transactional
    public PageableAdvancedDto<EcoNewsGenericDto> findRelevantEcoNews(Pageable pageable,
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
                0,
                (int) (totalEcoNewsCount / pageable.getPageSize()),
                ZonedDateTime.now()
            );
            userRelevanceNewsCache.put(key, cachedUserRelevantNews);
        }

        CachedRelevancePools cachedPools = cachedUserRelevantNews.getNewsRelevancePools();
        Map<Integer, List<Long>> relevantNewsPages = cachedUserRelevantNews.getRelevantNewsPages();
        int page = pageable.getPageNumber();
        List<EcoNews> findResult;

        if (relevantNewsPages.containsKey(page)) {
            findResult = ecoNewsRepo.findAllById(relevantNewsPages.get(page));
        } else {
            RelevancePools pools = getRelevancePoolsFromCache(cachedPools);
            CachedUserProfile userProfile = userProfileCache.getIfPresent(user.getId());
            if (userProfile == null) {
                userProfile = getAverageVectorsByUser(user.getId());
                userProfileCache.put(user.getId(), userProfile);
            }
            findResult = getResultByRatio(key, cachedUserRelevantNews, pools, userProfile);
            relevantNewsPages.put(page, findResult.stream()
                .map(EcoNews::getId)
                .toList());
        }

        Page<EcoNewsGenericDto> pageResult = new PageImpl<>(findResult.stream()
            .map(ecoNewsGenericDtoMapper::convert)
            .toList(),
            pageable,
            cachedUserRelevantNews.getTotalPagesCount());
        return pageableAdvancedDtoMapper.convert(pageResult);
    }

    private RelevancePools getRelevancePoolsFromCache(CachedRelevancePools pools) {
        LinkedList<EcoNews> relevantStrongNews =
            new LinkedList<>(ecoNewsRepo.findAllById(pools.relevantStrongNewsIds()));
        LinkedList<EcoNews> relevantWeakNews = new LinkedList<>(ecoNewsRepo.findAllById(pools.relevantWeakNewsIds()));
        LinkedList<EcoNews> nonRelevantNews = new LinkedList<>(ecoNewsRepo.findAllById(pools.nonRelevantNewsIds()));
        return new RelevancePools(relevantStrongNews, relevantWeakNews, nonRelevantNews);
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

    private List<EcoNews> getResultByRatio(RelevantEcoNewsCacheKey requestMetadata,
                                           CachedUserRelevantNews cachedUserNews,
                                           RelevancePools pools,
                                           CachedUserProfile userProfile) {
        while (!hasEnoughNews(requestMetadata, pools)
            && cachedUserNews.getLastGeneratedPage() < cachedUserNews.getTotalPagesCount()) {
            loadMoreNews(requestMetadata, cachedUserNews, pools, userProfile);
            cachedUserNews.setLastGeneratedPage(cachedUserNews.getLastGeneratedPage() + 1);
        }
        int[] ratioForPages = calculateRatioCounts(requestMetadata.pageSize(), relevancePoolsRatio);

        List<EcoNews> result = new ArrayList<>();

        result.addAll(pools.relevantWeakNews().stream()
                .limit(ratioForPages[0])
                .toList());
        result.addAll(pools.relevantStrongNews().stream()
                .limit(ratioForPages[1])
                .toList());
        result.addAll(pools.nonRelevantNews().stream()
                .limit(ratioForPages[2])
                .toList());
        return result;
    }

    private boolean hasEnoughNews(RelevantEcoNewsCacheKey requestMetadata,
                                  RelevancePools pools) {
        int[] ratioForPages = Arrays.stream(relevancePoolsRatio)
            .mapToInt(d -> (int) Math.round(d * requestMetadata.pageSize()))
            .toArray();
        return pools.relevantStrongNews().size() >= ratioForPages[0]
            && pools.relevantWeakNews().size() >= ratioForPages[1]
            && pools.nonRelevantNews().size() >= ratioForPages[2];
    }

    private void loadMoreNews(RelevantEcoNewsCacheKey requestMetadata,
                              CachedUserRelevantNews cachedUserNews,
                              RelevancePools pools,
                              CachedUserProfile userProfile) {
        List<EcoNews> news = getFilteredNews(requestMetadata, cachedUserNews.getLastRequestedDate());
        cachedUserNews.setLastRequestedDate(cachedUserNews.getLastRequestedDate().minusWeeks(1));
        List<EcoNewsRelevance> ecoNewsRelevanceList = ecoNewsRelevanceRepo.findAllByEcoNewsIdIn(
            news.stream()
                .map(EcoNews::getId)
                .toList());
        Map<Long, EcoNewsRelevance> ecoNewsRelevanceMap = ecoNewsRelevanceList.stream()
            .collect(Collectors.toMap(relevance -> relevance.getEcoNews().getId(),
                Function.identity()));
        List<EcoNewsWithRelevanceVectors> newsWithVectors = news.stream()
            .map(newsItem -> new EcoNewsWithRelevanceVectors(userProfile, newsItem,
                ecoNewsRelevanceMap.get(newsItem.getId())))
            .toList();
        newsWithVectors.stream()
            .sorted(Comparator.comparingDouble(EcoNewsWithRelevanceVectors::getRelevanceScore))
            .forEach(newsItem -> {
                if (newsItem.getRelevanceScore() > relevanceScoresStrength[0]) {
                    pools.relevantStrongNews().add(newsItem.getEcoNews());
                } else if (newsItem.getRelevanceScore() < relevanceScoresStrength[1]) {
                    pools.nonRelevantNews().add(newsItem.getEcoNews());
                } else {
                    pools.relevantWeakNews().add(newsItem.getEcoNews());
                }
            });
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
    private int[] calculateRatioCounts(int pageSize, double[] ratio) {
        int[] result = new int[ratio.length];
        double sum = Arrays.stream(ratio).sum();

        double[] exactCounts = new double[ratio.length];
        int total = 0;

        for (int i = 0; i < ratio.length; i++) {
            exactCounts[i] = ratio[i] / sum * pageSize;
            result[i] = (int) Math.floor(exactCounts[i]);
            total += result[i];
        }

        int remaining = pageSize - total;

        while (remaining > 0) {
            int bestIndex = -1;
            double maxFraction = -1;

            for (int i = 0; i < ratio.length; i++) {
                double fraction = exactCounts[i] - result[i];
                if (fraction > maxFraction) {
                    maxFraction = fraction;
                    bestIndex = i;
                }
            }

            if (bestIndex != -1) {
                result[bestIndex]++;
                remaining--;
            } else {
                break;
            }
        }

        return result;
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
        private Float[] tagsVector;
        private Float[] titleVector;
        private double relevanceScore;

        public EcoNewsWithRelevanceVectors(EcoNews ecoNews,
                                           EcoNewsRelevance ecoNewsRelevance) {
            this.ecoNews = ecoNews;
            if (ecoNews.getTags() != null) {
                this.tagsVector = new Float[ecoNewsTags.size()];
                ecoNews.getTags().forEach(tag -> tagsVector[ecoNewsTags.indexOf(tag)] = 1f);
            }
            if (ecoNewsRelevance != null) {
                this.titleVector = ecoNewsRelevance.getTitleVector();
            }
        }

        public EcoNewsWithRelevanceVectors(CachedUserProfile userProfile,
                                           EcoNews ecoNews,
                                           EcoNewsRelevance ecoNewsRelevance) {
            this(ecoNews, ecoNewsRelevance);
            double tagsRelevance = tagsVector == null ? 0.0 : relevanceScoresWeights[0]
                    * getCosineSimilarity(tagsVector, userProfile.tagsPreferencesVector());
            double titleRelevance = titleVector == null ? 0.0 : relevanceScoresWeights[1]
                    * getCosineSimilarity(titleVector, userProfile.titlePreferencesVector());
            this.relevanceScore = tagsRelevance + titleRelevance;
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

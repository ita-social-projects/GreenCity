package greencity.service;

import greencity.utils.RelevanceWeightUtils;
import greencity.dto.cache.CachedRelevancePools;
import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.dto.cache.CachedUserRelevantNews;
import greencity.dto.cache.RelevantEcoNewsCacheKey;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.relevance.EcoNewsWithRelevanceVectorsDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.exception.exceptions.EcoNewsRelevanceCalculationException;
import greencity.filters.EcoNewsSpecification;
import greencity.entity.EcoNewsRelevance;
import greencity.mapping.PageableAdvancedDtoMapper;
import greencity.repository.*;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EcoNewsRelevanceServiceImpl implements EcoNewsRelevanceService {
    private final EcoNewsRepo ecoNewsRepo;
    private final EcoNewsServiceImpl ecoNewsService;
    private final EcoNewsRelevanceRepo ecoNewsRelevanceRepo;
    private final CacheService cacheService;
    private final ModelMapper modelMapper;
    private final PageableAdvancedDtoMapper<EcoNewsGenericDto> pageableAdvancedDtoMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Value("${greencity.relevant.news.ratio}")
    private String relevancePoolsRatioString;
    @Value("${greencity.titles.tags.scores.weights}")
    private String relevanceScoresWeightsString;
    @Value("${greencity.relevance.strengths.threshold}")
    private String relevanceScoresStrengthString;
    private double[] relevancePoolsRatio;
    private double[] relevanceScoresWeights;
    private double[] relevanceScoresStrength;

    @PostConstruct
    public void init() {
        this.relevancePoolsRatio = RelevanceWeightUtils.convertRatioFromString(relevancePoolsRatioString);
        if (relevancePoolsRatio.length != 3) {
            throw new BeanInitializationException(String.format("Invalid relevance pools ratio parameter value. "
                    + "Expected 3 values in format 'a:b:c', but got %s.", relevancePoolsRatioString));
        }

        this.relevanceScoresWeights = RelevanceWeightUtils.convertRatioFromString(relevanceScoresWeightsString);
        if (relevanceScoresWeights.length != 2) {
            throw new BeanInitializationException(String.format("Invalid relevance scores weights parameter value. "
                    + "Expected 2 values in format 'a:b', but got %s.", relevanceScoresWeightsString));
        }

        this.relevanceScoresStrength = RelevanceWeightUtils.convertRatioFromString(relevanceScoresStrengthString);
        if (relevanceScoresStrength.length != 2) {
            throw new BeanInitializationException(String.format("Invalid relevance scores strength parameter value. "
                    + "Expected 2 values in format 'a:b', but got %s.", relevanceScoresStrengthString));
        }
    }

    @Override
    @Transactional
    public PageableAdvancedDto<EcoNewsGenericDto> findRelevantEcoNews(Pageable pageable,
                                                                      List<String> tags,
                                                                      String title,
                                                                      String author,
                                                                      UserVO user) {
        String tagsString = String.join(",", tags);
        RelevantEcoNewsCacheKey key = new RelevantEcoNewsCacheKey(
            user.getId(),
            tagsString,
            title,
            author,
            pageable.getPageSize());
        CachedUserRelevanceProfile userProfile = cacheService.getUserProfileFromCache(user.getId());
        List<EcoNews> findResult;
        long totalEcoNewsCount;

        if (userProfile.tagsPreferencesVector().length == 0
            && userProfile.titlePreferencesVector().length == 0) {
            EcoNewsViewDto filter = EcoNewsViewDto.builder()
                .title(title)
                .author(author)
                .tags(tagsString)
                .build();
            Page<EcoNews> findResultPage = ecoNewsRepo.findAll(ecoNewsService.getSpecification(filter), pageable);
            findResult = findResultPage.getContent();
            totalEcoNewsCount = findResultPage.getTotalElements();
        } else {
            CachedUserRelevantNews cachedUserRelevantNews = cacheService.getUserRelevantNewsFromCache(key);
            totalEcoNewsCount = cachedUserRelevantNews.getTotalNewsCount();
            Map<Integer, List<Long>> relevantNewsPages = cachedUserRelevantNews.getRelevantNewsPages();
            int page = pageable.getPageNumber();

            if (page > cachedUserRelevantNews.getLastGeneratedPage() + 1) {
                throw new EcoNewsRelevanceCalculationException(String.format(
                    "Requested page hasn't been generated yet."
                    + " Relevant news should be generated one by one to ensure consistent relevance."
                    + " Available pages 0-%d.", cachedUserRelevantNews.getLastGeneratedPage() + 1));
            } else if (relevantNewsPages.containsKey(page)) {
                findResult = ecoNewsRepo.findAllById(relevantNewsPages.get(page));
            } else {
                CachedTagsWithCoherence cachedTags = cacheService.getTagsCoherenceFromCache();
                findResult = getResultByRatio(key, cachedUserRelevantNews,
                    cachedUserRelevantNews.getNewsRelevancePools(), userProfile, cachedTags);
                relevantNewsPages.put(page, findResult.stream()
                    .map(EcoNews::getId)
                    .toList());
            }
        }

        Page<EcoNewsGenericDto> pageResult = new PageImpl<>(findResult.stream()
            .map(ecoNews -> modelMapper.map(ecoNews, EcoNewsGenericDto.class))
            .toList(),
            pageable,
            totalEcoNewsCount);
        return pageableAdvancedDtoMapper.convert(pageResult);
    }

    private List<EcoNews> getResultByRatio(RelevantEcoNewsCacheKey requestMetadata,
                                           CachedUserRelevantNews cachedUserNews,
                                           CachedRelevancePools pools,
                                           CachedUserRelevanceProfile userProfile,
                                           CachedTagsWithCoherence tags) {
        while (!hasEnoughNews(requestMetadata, pools)
            && cachedUserNews.getLastGeneratedPage() < cachedUserNews.getTotalPagesCount() - 1) {
            loadMoreNews(requestMetadata, cachedUserNews, pools, userProfile, tags);
        }
        cachedUserNews.setLastGeneratedPage(cachedUserNews.getLastGeneratedPage() + 1);
        double[] normalized = RelevanceWeightUtils.normalizeWeights(relevancePoolsRatio);
        int[] ratioForPages = RelevanceWeightUtils.distributeCounts(requestMetadata.pageSize(), normalized);
        return getNewsFromPoolsByRatio(pools, ratioForPages);
    }

    private boolean hasEnoughNews(RelevantEcoNewsCacheKey requestMetadata,
                                  CachedRelevancePools pools) {
        double[] normalized = RelevanceWeightUtils.normalizeWeights(relevancePoolsRatio);
        int[] ratioForPages = RelevanceWeightUtils.distributeCounts(requestMetadata.pageSize(), normalized);
        return pools.relevantStrongNewsIds().size() >= ratioForPages[0]
            && pools.relevantWeakNewsIds().size() >= ratioForPages[1]
            && pools.nonRelevantNewsIds().size() >= ratioForPages[2];
    }

    private void loadMoreNews(RelevantEcoNewsCacheKey requestMetadata,
                              CachedUserRelevantNews cachedUserNews,
                              CachedRelevancePools pools,
                              CachedUserRelevanceProfile userProfile,
                              CachedTagsWithCoherence tags) {
        List<EcoNews> news = getFilteredNews(requestMetadata, cachedUserNews.getLastRequestedDate());
        cachedUserNews.setLastRequestedDate(cachedUserNews.getLastRequestedDate().minusWeeks(1));
        List<EcoNewsRelevance> ecoNewsRelevanceList = ecoNewsRelevanceRepo.findAllByEcoNewsIdIn(
            news.stream()
                .map(EcoNews::getId)
                .toList());
        Map<Long, EcoNewsRelevance> ecoNewsRelevanceMap = ecoNewsRelevanceList.stream()
            .collect(Collectors.toMap(relevance -> relevance.getEcoNews().getId(),
                Function.identity()));
        List<EcoNewsWithRelevanceVectorsDto> newsWithVectors = news.stream()
            .map(newsItem -> new EcoNewsWithRelevanceVectorsDto(
                newsItem,
                ecoNewsRelevanceMap.get(newsItem.getId()),
                tags.ecoNewsTagsIndexes(),
                userProfile,
                relevanceScoresWeights)
            )
            .toList();
        newsWithVectors.stream()
            .sorted(Comparator.comparingDouble(EcoNewsWithRelevanceVectorsDto::getRelevanceScore).reversed())
            .forEach(newsItem -> {
                if (newsItem.getRelevanceScore() > relevanceScoresStrength[0]) {
                    pools.relevantStrongNewsIds().add(newsItem.getEcoNews().getId());
                } else if (newsItem.getRelevanceScore() < relevanceScoresStrength[1]) {
                    pools.nonRelevantNewsIds().add(newsItem.getEcoNews().getId());
                } else {
                    pools.relevantWeakNewsIds().add(newsItem.getEcoNews().getId());
                }
            });
    }

    private List<EcoNews> getFilteredNews(RelevantEcoNewsCacheKey requestMetadata, LocalDate lastRequestedDate) {
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

    private List<EcoNews> getNewsFromPoolsByRatio(CachedRelevancePools pools, int[] ratioForPages) {
        List<Long> resultPageIds = new ArrayList<>();
        for (int i = 0; i < ratioForPages[0]; i++) {
            resultPageIds.add(pools.relevantStrongNewsIds().poll());
        }
        for (int i = 0; i < ratioForPages[1]; i++) {
            resultPageIds.add(pools.relevantWeakNewsIds().poll());
        }
        for (int i = 0; i < ratioForPages[2]; i++) {
            resultPageIds.add(pools.nonRelevantNewsIds().poll());
        }
        return ecoNewsRepo.findAllById(resultPageIds);
    }
}

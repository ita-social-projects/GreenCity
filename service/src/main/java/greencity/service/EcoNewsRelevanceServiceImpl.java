package greencity.service;

import static greencity.constant.ErrorMessage.INVALID_RELEVANCE_POOLS;
import static greencity.constant.ErrorMessage.INVALID_SCORES_STRENGTH;
import static greencity.constant.ErrorMessage.INVALID_SCORES_WEIGHTS;
import greencity.constant.ErrorMessage;
import greencity.dto.econews.EcoNewsVO;
import greencity.entity.Tag;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
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
import jakarta.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
        double[] parsedRatio = RelevanceWeightUtils.parseAndValidateRatios(relevancePoolsRatioString,
            3, INVALID_RELEVANCE_POOLS, false, false);
        this.relevancePoolsRatio = RelevanceWeightUtils.normalizeWeights(parsedRatio);
        this.relevanceScoresWeights = RelevanceWeightUtils.parseAndValidateRatios(relevanceScoresWeightsString,
            2, INVALID_SCORES_WEIGHTS, true, true);
        this.relevanceScoresStrength = RelevanceWeightUtils.parseAndValidateRatios(relevanceScoresStrengthString,
            2, INVALID_SCORES_STRENGTH, true, false);
    }

    /**
     * Returns a pageable list of eco news relevant to the given user and filter
     * criteria.
     *
     * <p>
     * The method uses cached user profiles and relevance results if available. If
     * the user's relevance vectors are empty, it falls back to a generic search
     * using the provided title, author, and tags. Otherwise, it retrieves relevant
     * news from cached pages or generates new results by applying the relevance
     * scoring algorithm.
     * </p>
     *
     * @param pageable pagination and sorting information
     * @param tags     list of tags to filter news
     * @param title    title filter for eco news
     * @param author   author filter for eco news
     * @param user     the user for whom the relevance is calculated
     * @return a pageable DTO containing eco news relevant to the user
     * @throws EcoNewsRelevanceCalculationException if the requested page exceeds
     *                                              the last generated relevance
     *                                              page
     */
    @Override
    @Transactional
    public PageableAdvancedDto<EcoNewsGenericDto> findRelevantEcoNews(Pageable pageable,
        List<String> tags,
        String title,
        String author,
        UserVO user) {
        String tagsString = tags == null ? "" : String.join(",", tags);
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
            throw new EcoNewsRelevanceCalculationException(ErrorMessage.USER_HAS_NO_INTERACTIONS_YET);
        } else {
            CachedUserRelevantNews cachedUserRelevantNews = cacheService.getUserRelevantNewsFromCache(key);
            totalEcoNewsCount = cachedUserRelevantNews.getTotalNewsCount();
            if (totalEcoNewsCount == 0) {
                throw new NotFoundException(ErrorMessage.RELEVANT_NEWS_FOR_MONTH_NOT_FOUND);
            }

            Map<Integer, List<Long>> relevantNewsPages = cachedUserRelevantNews.getRelevantNewsPages();
            int page = pageable.getPageNumber();

            if (page > cachedUserRelevantNews.getLastGeneratedPage() + 1) {
                throw new EcoNewsRelevanceCalculationException(String.format(
                    ErrorMessage.INCONSISTENT_ORDER_OF_RELEVANT_NEWS,
                    cachedUserRelevantNews.getLastGeneratedPage() + 1));
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

    /**
     * {@inheritDoc}
     *
     * @param ecoNews eco news to mark relevance as outdated
     */
    @Override
    @Transactional
    public void markRelevanceAsOutdated(EcoNewsVO ecoNews) {
        if (ecoNews != null) {
            ecoNewsRelevanceRepo.setOutdatedTrueByEcoNewsId(ecoNews.getId());
        }
    }

    /**
     * Loads additional eco news and retrieves a list of news items based on
     * relevance pool ratios.
     *
     * <p>
     * Ensures that enough news is available in the relevance pools to fill the
     * current page. If necessary, invokes loading of more news and updates the
     * generated page index.
     * </p>
     *
     * @param requestMetadata metadata describing the current relevance request
     * @param cachedUserNews  the cache entry holding previous results
     * @param pools           the pools of eco news grouped by relevance strength
     * @param userProfile     the user's profile containing relevance vectors
     * @param tags            tag coherence data used for relevance scoring
     * @return a list of eco news for the current page, selected by configured ratio
     */
    private List<EcoNews> getResultByRatio(RelevantEcoNewsCacheKey requestMetadata,
        CachedUserRelevantNews cachedUserNews,
        CachedRelevancePools pools,
        CachedUserRelevanceProfile userProfile,
        CachedTagsWithCoherence tags) {
        while (ecoNewsRepo.countEcoNewsBetweenDates(cachedUserNews.getMinimumAvailableDate(),
            cachedUserNews.getLastRequestedDate()) != 0
            && !hasEnoughNews(requestMetadata, pools)
            && cachedUserNews.getLastGeneratedPage() < cachedUserNews.getTotalPagesCount() - 1) {
            loadMoreNews(requestMetadata, cachedUserNews, pools, userProfile, tags);
        }
        cachedUserNews.setLastGeneratedPage(cachedUserNews.getLastGeneratedPage() + 1);
        double[] normalized = RelevanceWeightUtils.normalizeWeights(relevancePoolsRatio);
        int[] ratioForPages = RelevanceWeightUtils.distributeCounts(requestMetadata.pageSize(), normalized);
        return getNewsFromPoolsByRatio(pools, ratioForPages);
    }

    /**
     * Checks whether the relevance pools contain enough news to fill a full result
     * page, according to the configured ratio of strong, weak, and non-relevant
     * news.
     *
     * @param requestMetadata metadata describing the current request
     * @param pools           the current state of relevance pools
     * @return true if each pool has enough items to meet the required ratio for the
     *         page; false otherwise
     */
    private boolean hasEnoughNews(RelevantEcoNewsCacheKey requestMetadata,
        CachedRelevancePools pools) {
        double[] normalized = RelevanceWeightUtils.normalizeWeights(relevancePoolsRatio);
        int[] ratioForPages = RelevanceWeightUtils.distributeCounts(requestMetadata.pageSize(), normalized);
        return pools.relevantStrongNewsIds().size() >= ratioForPages[0]
            && pools.relevantWeakNewsIds().size() >= ratioForPages[1]
            && pools.nonRelevantNewsIds().size() >= ratioForPages[2];
    }

    /**
     * Loads additional eco news items from the repository, evaluates their
     * relevance, and adds them to the appropriate relevance pool (strong, weak, or
     * non-relevant).
     *
     * <p>
     * This method decreases the date window by one week for each call to
     * progressively search older news. Relevance scores are calculated using vector
     * similarity and configured weights.
     * </p>
     *
     * @param requestMetadata metadata describing the current request
     * @param cachedUserNews  the cache object containing the current request state
     * @param pools           the target relevance pools to populate
     * @param userProfile     the user's profile containing relevance vectors
     * @param tags            tag coherence data used for scoring
     */
    private void loadMoreNews(RelevantEcoNewsCacheKey requestMetadata,
        CachedUserRelevantNews cachedUserNews,
        CachedRelevancePools pools,
        CachedUserRelevanceProfile userProfile,
        CachedTagsWithCoherence tags) {
        List<EcoNews> news = getFilteredNews(requestMetadata, cachedUserNews.getLastRequestedDate());
        cachedUserNews.setLastRequestedDate(cachedUserNews.getLastRequestedDate().minusWeeks(1));
        List<Long> ecoNewsIds = news.stream()
            .map(EcoNews::getId)
            .toList();
        List<EcoNewsRelevance> ecoNewsRelevanceList = ecoNewsIds.isEmpty()
            ? List.of()
            : ecoNewsRelevanceRepo.findAllByEcoNewsIdIn(ecoNewsIds);
        Map<Long, EcoNewsRelevance> ecoNewsRelevanceMap = ecoNewsRelevanceList.stream()
            .collect(Collectors.toMap(relevance -> relevance.getEcoNews().getId(),
                Function.identity()));
        List<EcoNewsWithRelevanceVectorsDto> newsWithVectors = news.stream()
            .map(newsItem -> {
                List<Long> tagIds = Optional.ofNullable(newsItem.getTags())
                    .orElse(Collections.emptyList()).stream()
                    .map(Tag::getId)
                    .toList();
                EcoNewsRelevance ecoNewsRelevance = ecoNewsRelevanceMap.get(newsItem.getId());
                Float[] titleVector = null;
                if (ecoNewsRelevance != null && !ecoNewsRelevance.getIsOutdated()) {
                    titleVector = ecoNewsRelevance.getTitleVector();
                }
                return new EcoNewsWithRelevanceVectorsDto(
                    newsItem.getId(),
                    tagIds,
                    titleVector,
                    tags.ecoNewsTagsIndexes(),
                    userProfile,
                    relevanceScoresWeights);
            })
            .toList();
        newsWithVectors.stream()
            .sorted(Comparator.comparingDouble(EcoNewsWithRelevanceVectorsDto::getRelevanceScore).reversed())
            .forEach(newsItem -> {
                if (newsItem.getRelevanceScore() > relevanceScoresStrength[0]) {
                    pools.relevantStrongNewsIds().add(newsItem.getEcoNewsId());
                } else if (newsItem.getRelevanceScore() < relevanceScoresStrength[1]) {
                    pools.nonRelevantNewsIds().add(newsItem.getEcoNewsId());
                } else {
                    pools.relevantWeakNewsIds().add(newsItem.getEcoNewsId());
                }
            });
    }

    /**
     * Retrieves eco news from the database using the provided filter metadata and
     * date range.
     *
     * <p>
     * The filter includes title, author, tags, and a dynamic date window ending at
     * the last requested date. The start date is calculated by subtracting one
     * week.
     * </p>
     *
     * @param requestMetadata   metadata containing filter values
     * @param lastRequestedDate the end date of the filtering range
     * @return a list of eco news matching the specified filters and date range
     */
    private List<EcoNews> getFilteredNews(RelevantEcoNewsCacheKey requestMetadata, ZonedDateTime lastRequestedDate) {
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

    /**
     * Retrieves a list of eco news IDs from the relevance pools based on the
     * provided distribution ratio.
     *
     * <p>
     * The method polls items from each relevance queue (strong, weak, non-relevant)
     * based on how many items should be taken from each category according to the
     * ratio.
     * </p>
     *
     * @param pools         the current relevance pools
     * @param ratioForPages the number of news to retrieve from each pool [strong,
     *                      weak, non-relevant]
     * @return a list of eco news entities corresponding to the selected IDs
     */
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

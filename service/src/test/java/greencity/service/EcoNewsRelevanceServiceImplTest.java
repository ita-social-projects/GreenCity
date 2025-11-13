package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.cache.CachedRelevancePools;
import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.dto.cache.CachedUserRelevantNews;
import greencity.dto.cache.RelevantEcoNewsCacheKey;
import greencity.dto.econews.EcoNewsGenericDto;
import greencity.dto.econews.EcoNewsViewDto;
import greencity.dto.relevance.EcoNewsWithRelevanceVectorsDto;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import greencity.exception.exceptions.EcoNewsRelevanceCalculationException;
import greencity.exception.exceptions.NotFoundException;
import greencity.filters.EcoNewsSpecification;
import greencity.mapping.PageableAdvancedDtoMapper;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EcoNewsRelevanceServiceImplTest {
    @Mock
    private CacheService cacheService;

    @Mock
    private EcoNewsServiceImpl ecoNewsService;

    @Mock
    private EcoNewsRepo ecoNewsRepo;

    @Mock
    private EcoNewsRelevanceRepo ecoNewsRelevanceRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PageableAdvancedDtoMapper pageableAdvancedDtoMapper;

    @InjectMocks
    private EcoNewsRelevanceServiceImpl ecoNewsRelevanceService;

    private final double[] relevancePoolsRatio = {0.5, 0.3, 0.2};
    private final double[] relevanceScoresWeights = {0.7, 0.3};
    private final double[] relevanceScoresStrength = {0.8, 0.2};
    private static final DateTimeFormatter MOCKED_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(ecoNewsRelevanceService, "relevancePoolsRatio", relevancePoolsRatio);
        ReflectionTestUtils.setField(ecoNewsRelevanceService, "relevanceScoresWeights", relevanceScoresWeights);
        ReflectionTestUtils.setField(ecoNewsRelevanceService, "relevanceScoresStrength", relevanceScoresStrength);
        ReflectionTestUtils.setField(ecoNewsRelevanceService, "formatter", MOCKED_FORMATTER);
    }

    @Test
    void init_withValidRatios_shouldInitializeArrays() throws Exception {
        EcoNewsRelevanceServiceImpl service = new EcoNewsRelevanceServiceImpl(null, null, null, null, null, null);

        setPrivateField(service, "relevancePoolsRatioString", "3:5:2");
        setPrivateField(service, "relevanceScoresWeightsString", "0.7:0.3");
        setPrivateField(service, "relevanceScoresStrengthString", "0.6:0.4");

        service.init();

        double[] poolsRatio = (double[]) getPrivateField(service, "relevancePoolsRatio");
        double[] scoresWeights = (double[]) getPrivateField(service, "relevanceScoresWeights");
        double[] scoresStrength = (double[]) getPrivateField(service, "relevanceScoresStrength");

        assertArrayEquals(new double[] {0.3, 0.5, 0.2}, poolsRatio);
        assertArrayEquals(new double[] {0.7, 0.3}, scoresWeights);
        assertArrayEquals(new double[] {0.6, 0.4}, scoresStrength);
    }

    @Test
    void init_withInvalidPoolsRatio_shouldThrow() throws Exception {
        EcoNewsRelevanceServiceImpl service = new EcoNewsRelevanceServiceImpl(null, null, null, null, null, null);

        setPrivateField(service, "relevancePoolsRatioString", "0.5:0.5");
        setPrivateField(service, "relevanceScoresWeightsString", "0.7:0.3");
        setPrivateField(service, "relevanceScoresStrengthString", "0.6:0.2");

        BeanInitializationException ex = assertThrows(BeanInitializationException.class, service::init);
        assertTrue(ex.getMessage().contains("Expected 3 values"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.5", "0.5:0.4"})
    void init_withInvalidScoresWeights_shouldThrow(String scoresWeights) throws Exception {
        EcoNewsRelevanceServiceImpl service = new EcoNewsRelevanceServiceImpl(null, null, null, null, null, null);

        setPrivateField(service, "relevancePoolsRatioString", "0.3:0.5:0.2");
        setPrivateField(service, "relevanceScoresWeightsString", scoresWeights);
        setPrivateField(service, "relevanceScoresStrengthString", "0.6:0.2");

        assertThrows(BeanInitializationException.class, service::init);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.6"})
    void init_withInvalidScoresStrength_shouldThrow(String scoresStrength) throws Exception {
        EcoNewsRelevanceServiceImpl service = new EcoNewsRelevanceServiceImpl(null, null, null, null, null, null);

        setPrivateField(service, "relevancePoolsRatioString", "0.3:0.5:0.2");
        setPrivateField(service, "relevanceScoresWeightsString", "0.7:0.3");
        setPrivateField(service, "relevanceScoresStrengthString", scoresStrength);

        assertThrows(BeanInitializationException.class, service::init);
    }

    @Test
    void findRelevantEcoNews_emptyUserProfile_callsFindAll() {
        Long userId = 1L;
        List<String> tags = List.of("nature");
        String title = "Eco";
        String author = "John";
        int pageSize = 10;
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

        UserVO user = new UserVO();
        user.setId(userId);
        CachedUserRelevanceProfile cachedUserRelevanceProfile = new CachedUserRelevanceProfile(
            new Float[0], new Float[0]);

        when(cacheService.getUserProfileFromCache(anyLong())).thenReturn(cachedUserRelevanceProfile);

        assertThrows(EcoNewsRelevanceCalculationException.class, () -> ecoNewsRelevanceService.findRelevantEcoNews(
            pageable, tags, title, author, user));

        verify(cacheService).getUserProfileFromCache(anyLong());
        verify(cacheService, never()).getUserRelevantNewsFromCache(any());
    }

    @Test
    void findRelevantEcoNews_pageNotExistsInCache_throwsException() {
        Long userId = 1L;
        List<String> tags = List.of("nature");
        String title = "Eco";
        String author = "John";
        int pageSize = 10;
        int pageNumber = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

        UserVO user = new UserVO();
        user.setId(userId);
        List<Long> newsIds = List.of(100L, 101L);

        CachedUserRelevanceProfile cachedUserRelevanceProfile = ModelUtils.getCachedUserRelevanceProfile();
        CachedUserRelevantNews cachedUserRelevantNews = mock(CachedUserRelevantNews.class);

        when(cacheService.getUserProfileFromCache(anyLong())).thenReturn(cachedUserRelevanceProfile);
        when(cacheService.getUserRelevantNewsFromCache(any())).thenReturn(cachedUserRelevantNews);

        assertThrows(NotFoundException.class, () -> ecoNewsRelevanceService.findRelevantEcoNews(
            pageable, tags, title, author, user));

        verify(cacheService).getUserProfileFromCache(anyLong());
        verify(cacheService).getUserRelevantNewsFromCache(any());
        verify(ecoNewsRepo, never()).findAllById(newsIds);
        verify(modelMapper, never()).map(any(), any());
        verify(pageableAdvancedDtoMapper, never()).convert(any(Page.class));
    }

    @Test
    void findRelevantEcoNews_pageExistsInCache_returnsDto() {
        Long userId = 1L;
        List<String> tags = List.of("nature");
        String title = "Eco";
        String author = "John";
        int pageSize = 10;
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

        UserVO user = new UserVO();
        user.setId(userId);

        List<Long> newsIds = List.of(100L, 101L);
        List<EcoNews> ecoNewsList = newsIds.stream()
            .map(id -> EcoNews.builder().id(id).build())
            .toList();

        CachedUserRelevanceProfile cachedUserRelevanceProfile = ModelUtils.getCachedUserRelevanceProfile();
        CachedUserRelevantNews cachedUserRelevantNews = mock(CachedUserRelevantNews.class);
        Map<Integer, List<Long>> relevantNewsPages = new HashMap<>();
        relevantNewsPages.put(pageNumber, newsIds);

        when(cacheService.getUserProfileFromCache(anyLong())).thenReturn(cachedUserRelevanceProfile);
        when(cacheService.getUserRelevantNewsFromCache(any())).thenReturn(cachedUserRelevantNews);
        when(ecoNewsRepo.findAllById(newsIds)).thenReturn(ecoNewsList);
        when(cachedUserRelevantNews.getRelevantNewsPages()).thenReturn(relevantNewsPages);
        when(cachedUserRelevantNews.getLastGeneratedPage()).thenReturn(-1);
        when(cachedUserRelevantNews.getTotalNewsCount()).thenReturn(10L);
        when(cachedUserRelevantNews.getTotalPagesCount()).thenReturn(1);

        EcoNewsGenericDto dto1 = mock(EcoNewsGenericDto.class);
        EcoNewsGenericDto dto2 = mock(EcoNewsGenericDto.class);

        Mockito.doAnswer(invocation -> {
            EcoNews source = invocation.getArgument(0);
            return source.getId().equals(100L) ? dto1 : dto2;
        }).when(modelMapper).map(Mockito.any(EcoNews.class), Mockito.<Class<EcoNewsGenericDto>>any());

        PageableAdvancedDto<EcoNewsGenericDto> expected = mock(PageableAdvancedDto.class);
        when(pageableAdvancedDtoMapper.convert(any(Page.class))).thenReturn(expected);

        PageableAdvancedDto<EcoNewsGenericDto> actual = ecoNewsRelevanceService.findRelevantEcoNews(
            pageable, tags, title, author, user);

        assertEquals(expected, actual);

        verify(cacheService).getUserProfileFromCache(anyLong());
        verify(cacheService).getUserRelevantNewsFromCache(any());
        verify(ecoNewsRepo).findAllById(newsIds);
        verify(modelMapper, times(2))
            .map(any(EcoNews.class), (Class<EcoNewsGenericDto>) any(Class.class));
        verify(pageableAdvancedDtoMapper).convert(any(Page.class));
    }

    @Test
    void findRelevantEcoNews_pageNotExistsInCache_loadsAndCachesNews_indirectTest() {
        Long userId = 1L;
        List<String> tags = List.of("nature");
        String title = "Eco";
        String author = "John";
        int pageSize = 10;
        int pageNumber = 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

        UserVO user = new UserVO();
        user.setId(userId);

        CachedUserRelevantNews cachedUserRelevantNews = mock(CachedUserRelevantNews.class);
        Map<Integer, List<Long>> relevantNewsPages = mock(Map.class);
        CachedRelevancePools pools = new CachedRelevancePools(
            new LinkedList<>(), new LinkedList<>(), new LinkedList<>());
        when(cachedUserRelevantNews.getRelevantNewsPages()).thenReturn(relevantNewsPages);
        when(cachedUserRelevantNews.getLastGeneratedPage()).thenReturn(-1);
        when(cachedUserRelevantNews.getMinimumAvailableDate()).thenReturn(ZonedDateTime.now().minusMonths(1));
        when(cachedUserRelevantNews.getLastRequestedDate()).thenReturn(ZonedDateTime.now());
        when(cachedUserRelevantNews.getNewsRelevancePools()).thenReturn(pools);
        when(cachedUserRelevantNews.getTotalNewsCount()).thenReturn(10L);
        when(cachedUserRelevantNews.getTotalPagesCount()).thenReturn(1);
        when(cacheService.getUserRelevantNewsFromCache(any())).thenReturn(cachedUserRelevantNews);

        CachedUserRelevanceProfile userProfile = ModelUtils.getCachedUserRelevanceProfile();
        when(cacheService.getUserProfileFromCache(userId)).thenReturn(userProfile);

        CachedTagsWithCoherence cachedTags = mock(CachedTagsWithCoherence.class);
        when(cachedTags.ecoNewsTagsIndexes()).thenReturn(new HashMap<>());
        when(cacheService.getTagsCoherenceFromCache()).thenReturn(cachedTags);

        EcoNewsSpecification mockSpecification = mock(EcoNewsSpecification.class);
        when(ecoNewsService.getSpecification(any(EcoNewsViewDto.class))).thenReturn(mockSpecification);

        List<EcoNews> initialFilteredNews = new ArrayList<>();
        List<Long> expectedNewsIdsInPage = new ArrayList<>();

        long currentId = 200L;
        for (int i = 0; i < 5; i++) {
            initialFilteredNews.add(EcoNews.builder().id(currentId++).build());
        }
        for (int i = 0; i < 3; i++) {
            initialFilteredNews.add(EcoNews.builder().id(currentId++).build());
        }
        for (int i = 0; i < 2; i++) {
            initialFilteredNews.add(EcoNews.builder().id(currentId++).build());
        }

        expectedNewsIdsInPage.add(200L);
        expectedNewsIdsInPage.add(201L);
        expectedNewsIdsInPage.add(202L);
        expectedNewsIdsInPage.add(203L);
        expectedNewsIdsInPage.add(204L);
        expectedNewsIdsInPage.add(205L);
        expectedNewsIdsInPage.add(206L);
        expectedNewsIdsInPage.add(207L);
        expectedNewsIdsInPage.add(208L);
        expectedNewsIdsInPage.add(209L);

        when(ecoNewsRepo.findAll(eq(mockSpecification), any(Sort.class)))
            .thenReturn(initialFilteredNews);
        when(ecoNewsRepo.countEcoNewsBetweenDates(any(ZonedDateTime.class), any(ZonedDateTime.class)))
            .thenReturn((long) initialFilteredNews.size());

        when(ecoNewsRelevanceRepo.findAllByEcoNewsIdIn(anyList()))
            .thenAnswer(invocation -> {
                List<Long> ids = invocation.getArgument(0);
                return ids.stream().map(id -> {
                    EcoNews ecoNews = initialFilteredNews.stream()
                        .filter(newsItem -> newsItem.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                    EcoNewsRelevance relevance = new EcoNewsRelevance();

                    relevance.setEcoNews(ecoNews);
                    return relevance;
                }).toList();
            });

        try (MockedConstruction<EcoNewsWithRelevanceVectorsDto> mockedConstruction =
            mockConstruction(EcoNewsWithRelevanceVectorsDto.class,
                (mock, context) -> {
                    Long newsItemId = (Long) context.arguments().get(0);
                    Double scoreToReturn;

                    if (newsItemId <= 204L) {
                        scoreToReturn = 0.9;
                    } else if (newsItemId <= 207L) {
                        scoreToReturn = 0.5;
                    } else {
                        scoreToReturn = 0.1;
                    }
                    when(mock.getRelevanceScore()).thenReturn(scoreToReturn);
                    when(mock.getEcoNewsId()).thenReturn(newsItemId);
                })) {

            when(ecoNewsRepo.findAllById(anyList()))
                .thenAnswer(invocation -> {
                    List<Long> ids = invocation.getArgument(0);
                    return ids.stream()
                        .map(id -> initialFilteredNews.stream()
                            .filter(news -> news.getId().equals(id))
                            .findFirst()
                            .orElse(null))
                        .toList();
                });

            EcoNewsGenericDto dtoPlaceholder = mock(EcoNewsGenericDto.class);
            when(modelMapper.map(any(EcoNews.class), Mockito.<Class<EcoNewsGenericDto>>any()))
                .thenReturn(dtoPlaceholder);

            PageableAdvancedDto<EcoNewsGenericDto> expectedDto = mock(PageableAdvancedDto.class);
            when(pageableAdvancedDtoMapper.convert(any(Page.class))).thenReturn(expectedDto);

            PageableAdvancedDto<EcoNewsGenericDto> actual = ecoNewsRelevanceService.findRelevantEcoNews(
                pageable, tags, title, author, user);

            assertEquals(expectedDto, actual);

            verify(cacheService).getUserRelevantNewsFromCache(any(RelevantEcoNewsCacheKey.class));
            verify(cachedUserRelevantNews).getTotalNewsCount();
            verify(cachedUserRelevantNews, times(2)).getTotalPagesCount();
            verify(cachedUserRelevantNews).getRelevantNewsPages();
            verify(cacheService).getUserProfileFromCache(userId);
            verify(cacheService).getTagsCoherenceFromCache();

            verify(ecoNewsService).getSpecification(any(EcoNewsViewDto.class));
            verify(ecoNewsRepo).findAll(eq(mockSpecification), any(Sort.class));
            verify(ecoNewsRelevanceRepo).findAllByEcoNewsIdIn(
                initialFilteredNews.stream().map(EcoNews::getId).toList());

            assertEquals(initialFilteredNews.size(), mockedConstruction.constructed().size());

            verify(cachedUserRelevantNews, atLeastOnce()).setLastGeneratedPage(anyInt());
            verify(ecoNewsRepo).findAllById(expectedNewsIdsInPage);
            verify(relevantNewsPages).put(pageNumber, expectedNewsIdsInPage);

            verify(modelMapper, times(pageSize))
                .map(any(EcoNews.class), (Class<EcoNewsGenericDto>) any(Class.class));
            verify(pageableAdvancedDtoMapper).convert(any(Page.class));
        }
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private Object getPrivateField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}

package greencity.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.benmanes.caffeine.cache.Cache;
import greencity.ModelUtils;
import greencity.utils.RelevanceWeightUtils;
import greencity.dto.cache.CachedRelevancePools;
import greencity.dto.cache.CachedTagsWithCoherence;
import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.dto.cache.CachedUserRelevantNews;
import greencity.dto.cache.RelevantEcoNewsCacheKey;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.Habit;
import greencity.entity.HabitAssign;
import greencity.entity.Tag;
import greencity.entity.TagsCoherence;
import greencity.entity.event.Event;
import greencity.repository.EcoNewsRelevanceRepo;
import greencity.repository.EcoNewsRepo;
import greencity.repository.EventRepo;
import greencity.repository.HabitAssignRepo;
import greencity.repository.TagsCoherenceRepo;
import greencity.repository.TagsRepo;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CacheServiceImplTest {
    @Mock
    private EcoNewsRepo ecoNewsRepo;
    @Mock
    private EcoNewsRelevanceRepo ecoNewsRelevanceRepo;
    @Mock
    private TagsRepo tagsRepo;
    @Mock
    private HabitAssignRepo habitAssignRepo;
    @Mock
    private EventRepo eventRepo;
    @Mock
    private TagsCoherenceRepo tagsCoherenceRepo;
    @Mock
    private Cache<RelevantEcoNewsCacheKey, CachedUserRelevantNews> userRelevanceNewsCache;
    @Mock
    private Cache<Long, CachedUserRelevanceProfile> userProfileCache;
    @Mock
    private Cache<Long, CachedTagsWithCoherence> tagsCoherenceCache;

    @InjectMocks
    private CacheServiceImpl cacheService;

    private RelevantEcoNewsCacheKey relevantEcoNewsCacheKey;
    private CachedUserRelevantNews cachedUserRelevantNews;
    private CachedUserRelevanceProfile cachedUserRelevanceProfile;
    private CachedTagsWithCoherence cachedTagsWithCoherence;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cacheService, "tagsWeights", new double[] {0.6, 0.2, 0.2});
        ReflectionTestUtils.setField(cacheService, "userRelevanceNewsCache", userRelevanceNewsCache);
        ReflectionTestUtils.setField(cacheService, "userProfileCache", userProfileCache);
        ReflectionTestUtils.setField(cacheService, "tagsCoherenceCache", tagsCoherenceCache);
        relevantEcoNewsCacheKey = ModelUtils.getRelevantEcoNewsCacheKey();
        cachedUserRelevantNews = ModelUtils.getCachedUserRelevantNews();
        cachedUserRelevanceProfile = ModelUtils.getCachedUserRelevanceProfile();
        cachedTagsWithCoherence = ModelUtils.getCachedTagsWithCoherence();
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.6:0.2:0.2", "1.0:1.0:1.0"})
    void initWithValidWeightsTest(String weightsString) {
        ReflectionTestUtils.setField(cacheService, "tagsWeightsString", weightsString);
        assertDoesNotThrow(() -> cacheService.init());
        assertArrayEquals(RelevanceWeightUtils.convertRatioFromString(weightsString),
            (double[]) ReflectionTestUtils.getField(cacheService, "tagsWeights"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0:0:0", "0.6:0.2", "1.0:1.0:1.0:1.0", "a:b:c", "0:0:A"})
    void initWithInvalidWeightsTest(String weightsString) {
        ReflectionTestUtils.setField(cacheService, "tagsWeightsString", weightsString);
        assertThrows(BeanInitializationException.class, () -> cacheService.init());
    }

    @Test
    void testGetUserRelevantNewsFromCacheWhenAlreadyCached() {
        when(userRelevanceNewsCache.getIfPresent(relevantEcoNewsCacheKey))
            .thenReturn(cachedUserRelevantNews);

        CachedUserRelevantNews result = cacheService.getUserRelevantNewsFromCache(relevantEcoNewsCacheKey);

        assertEquals(cachedUserRelevantNews, result);
        verify(userRelevanceNewsCache).getIfPresent(relevantEcoNewsCacheKey);
        verify(userRelevanceNewsCache, never()).put(any(), any());
    }

    @Test
    void testGetUserRelevantNewsFromCacheWhenNotCached() {
        when(userRelevanceNewsCache.getIfPresent(relevantEcoNewsCacheKey)).thenReturn(null);
        when(ecoNewsRepo.count()).thenReturn(9L);

        CachedUserRelevantNews result = cacheService.getUserRelevantNewsFromCache(relevantEcoNewsCacheKey);
        assertNotNull(result);

        CachedRelevancePools pools = result.getNewsRelevancePools();
        assertNotNull(pools);
        assertTrue(pools.relevantStrongNewsIds().isEmpty());
        assertTrue(pools.relevantWeakNewsIds().isEmpty());
        assertTrue(pools.nonRelevantNewsIds().isEmpty());
        assertTrue(result.getRelevantNewsPages().isEmpty());
        assertEquals(-1, result.getLastGeneratedPage());
        assertEquals((int) Math.ceil(9.0 / relevantEcoNewsCacheKey.pageSize()), result.getTotalPagesCount());
        assertEquals(9L, result.getTotalNewsCount());
        assertTrue(result.getLastRequestedDate().isAfter(ZonedDateTime.now()));
        assertTrue(result.getLastRequestedDate().isBefore(ZonedDateTime.now().plusDays(2)));
        verify(userRelevanceNewsCache).put(relevantEcoNewsCacheKey, result);
    }

    @Test
    void testGetUserProfileFromCacheWhenAlreadyCached() {
        Long userId = 1L;
        when(userProfileCache.getIfPresent(userId)).thenReturn(cachedUserRelevanceProfile);

        CachedUserRelevanceProfile result = cacheService.getUserProfileFromCache(userId);

        assertEquals(cachedUserRelevanceProfile, result);
        verify(userProfileCache).getIfPresent(userId);
        verify(userProfileCache, never()).put(any(), any());
    }

    @Test
    void testGetUserProfileFromCacheWhenNotCached() {
        Long userId = 1L;
        EcoNewsRelevance ecoNewsRelevance = ModelUtils.getEcoNewsRelevance();
        EcoNewsRelevance ecoNewsRelevanceZeroValue = ModelUtils.getEcoNewsRelevance();
        ecoNewsRelevanceZeroValue.getTitleVector()[0] = 0.0f;
        EcoNewsRelevance ecoNewsRelevanceNullVector = ModelUtils.getEcoNewsRelevance();
        ecoNewsRelevanceNullVector.setTitleVector(null);
        EcoNewsRelevance ecoNewsRelevanceEmptyVector = ModelUtils.getEcoNewsRelevance();
        ecoNewsRelevanceEmptyVector.setTitleVector(new Float[0]);
        EcoNewsRelevance ecoNewsRelevanceWrongDimension = ModelUtils.getEcoNewsRelevance();
        ecoNewsRelevanceWrongDimension.setTitleVector(new Float[] {0.6f, 0.9f, -1.0f});
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        Event event = ModelUtils.getEvent();
        prepareTagsCollections(ecoNewsRelevance.getEcoNews(), event, habitAssign.getHabit());

        when(userProfileCache.getIfPresent(userId)).thenReturn(null);
        when(tagsCoherenceCache.getIfPresent(any())).thenReturn(cachedTagsWithCoherence);
        when(ecoNewsRelevanceRepo.findLikedEcoNewsByUserId(userId))
            .thenReturn(List.of(ecoNewsRelevance, ecoNewsRelevanceZeroValue));
        when(habitAssignRepo.findAllByUserId(userId)).thenReturn(List.of(habitAssign));
        when(eventRepo.findLikedEventsByUserId(userId)).thenReturn(List.of(event));

        CachedUserRelevanceProfile result = cacheService.getUserProfileFromCache(userId);

        assertNotNull(result);
        assertNotNull(result.tagsPreferencesVector());
        assertEquals(5, result.tagsPreferencesVector().length);
        assertNotNull(result.titlePreferencesVector());
        assertEquals(10, result.titlePreferencesVector().length);
        verify(userProfileCache).put(eq(userId), any(CachedUserRelevanceProfile.class));
    }

    @Test
    void testGetUserProfileFromCacheWhenNotCachedAndEmptyLikedNews() {
        Long userId = 1L;
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        Event event = ModelUtils.getEvent();
        prepareTagsCollections(null, event, habitAssign.getHabit());

        when(userProfileCache.getIfPresent(userId)).thenReturn(null);
        when(tagsCoherenceCache.getIfPresent(any())).thenReturn(cachedTagsWithCoherence);
        when(ecoNewsRelevanceRepo.findLikedEcoNewsByUserId(userId)).thenReturn(List.of());
        when(habitAssignRepo.findAllByUserId(userId)).thenReturn(List.of(habitAssign));
        when(eventRepo.findLikedEventsByUserId(userId)).thenReturn(List.of(event));

        CachedUserRelevanceProfile result = cacheService.getUserProfileFromCache(userId);

        assertNotNull(result);
        assertNotNull(result.tagsPreferencesVector());
        assertEquals(5, result.tagsPreferencesVector().length);
        assertNotNull(result.titlePreferencesVector());
        assertEquals(0, result.titlePreferencesVector().length);
        verify(userProfileCache).put(eq(userId), any(CachedUserRelevanceProfile.class));
    }

    @Test
    void testGetUserProfileFromCacheWhenNotCachedAndInvalidHabitsAndEvents() {
        Long userId = 1L;
        EcoNewsRelevance ecoNewsRelevance = ModelUtils.getEcoNewsRelevance();

        when(userProfileCache.getIfPresent(userId)).thenReturn(null);
        when(tagsCoherenceCache.getIfPresent(any())).thenReturn(cachedTagsWithCoherence);
        when(ecoNewsRelevanceRepo.findLikedEcoNewsByUserId(userId)).thenReturn(List.of(ecoNewsRelevance));
        when(habitAssignRepo.findAllByUserId(userId)).thenReturn(List.of());
        when(eventRepo.findLikedEventsByUserId(userId)).thenReturn(List.of());

        CachedUserRelevanceProfile result = cacheService.getUserProfileFromCache(userId);

        assertNotNull(result);
        assertNotNull(result.tagsPreferencesVector());
        assertEquals(5, result.tagsPreferencesVector().length);
        assertNotNull(result.titlePreferencesVector());
        assertEquals(10, result.titlePreferencesVector().length);
        verify(userProfileCache).put(eq(userId), any(CachedUserRelevanceProfile.class));
    }

    @Test
    void testGetUserProfileFromCacheWhenNotCachedAndNoUserPreferenceData() {
        Long userId = 1L;
        EcoNewsRelevance ecoNewsRelevance = ModelUtils.getEcoNewsRelevance();
        ecoNewsRelevance.setTitleVector(null);
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        Event event = ModelUtils.getEvent();
        prepareEmptyTagsCollections(ecoNewsRelevance.getEcoNews(), event, habitAssign.getHabit());

        when(userProfileCache.getIfPresent(userId)).thenReturn(null);
        when(tagsCoherenceCache.getIfPresent(any())).thenReturn(cachedTagsWithCoherence);
        when(ecoNewsRelevanceRepo.findLikedEcoNewsByUserId(userId)).thenReturn(List.of(ecoNewsRelevance));
        when(habitAssignRepo.findAllByUserId(userId)).thenReturn(List.of(habitAssign));
        when(eventRepo.findLikedEventsByUserId(userId)).thenReturn(List.of(event));

        CachedUserRelevanceProfile result = cacheService.getUserProfileFromCache(userId);

        assertNotNull(result);
        assertNotNull(result.tagsPreferencesVector());
        assertEquals(0, result.tagsPreferencesVector().length);
        assertNotNull(result.titlePreferencesVector());
        assertEquals(0, result.titlePreferencesVector().length);
        verify(userProfileCache).put(eq(userId), any(CachedUserRelevanceProfile.class));
    }

    @Test
    void testGetTagsCoherenceFromCacheWhenAlreadyCached() {
        when(tagsCoherenceCache.getIfPresent(any())).thenReturn(cachedTagsWithCoherence);

        CachedTagsWithCoherence result = cacheService.getTagsCoherenceFromCache();

        assertEquals(cachedTagsWithCoherence, result);
        verify(tagsCoherenceCache).getIfPresent(any());
        verify(tagsCoherenceCache, never()).put(any(), any());
    }

    @ParameterizedTest
    @ValueSource(floats = {0.0f, 0.5f, 1.0f})
    void testGetTagsCoherenceFromCacheWhenNotCached(Float coherence) {
        Tag ecoNewsTag = ModelUtils.getTag();
        Tag eventTag = ModelUtils.getEventTag();
        Tag habitTag = ModelUtils.getHabitTag();
        Tag unknownTag = new Tag();
        when(tagsCoherenceCache.getIfPresent(any())).thenReturn(null);
        when(tagsRepo.findAll())
            .thenReturn(List.of(ecoNewsTag, eventTag, habitTag, unknownTag));
        when(tagsCoherenceRepo.findAll()).thenReturn(List.of(TagsCoherence.builder()
            .sourceTag(eventTag)
            .destinationTag(ecoNewsTag)
            .coherence(coherence)
            .build()));

        CachedTagsWithCoherence result = cacheService.getTagsCoherenceFromCache();

        assertNotNull(result);
        assertEquals(1, result.ecoNewsTagsIndexes().size());
        assertEquals(1, result.eventTagsIndexes().size());
        assertEquals(1, result.habitTagsIndexes().size());
        Map<Long, Float> coherenceEntry = result.tagsCoherenceIds().get(eventTag.getId());
        assertNotNull(coherenceEntry);
        assertNotNull(coherenceEntry.get(ecoNewsTag.getId()));
        assertEquals(coherence, coherenceEntry.get(ecoNewsTag.getId()));
        verify(tagsCoherenceCache).put(any(), any());
    }

    @Test
    void testGetTagsCoherenceFromCacheWhenNotCachedAndCoherenceMatrixIsEmpty() {
        when(tagsCoherenceCache.getIfPresent(any())).thenReturn(null);
        when(tagsRepo.findAll())
            .thenReturn(List.of(ModelUtils.getTag(), ModelUtils.getEventTag(), ModelUtils.getHabitTag()));
        when(tagsCoherenceRepo.findAll()).thenReturn(List.of());

        CachedTagsWithCoherence result = cacheService.getTagsCoherenceFromCache();

        assertNotNull(result);
        assertEquals(1, result.ecoNewsTagsIndexes().size());
        assertEquals(1, result.eventTagsIndexes().size());
        assertEquals(1, result.habitTagsIndexes().size());
        assertTrue(result.tagsCoherenceIds().isEmpty());
        verify(tagsCoherenceCache).put(any(), any());
    }

    private void prepareTagsCollections(EcoNews ecoNews, Event event, Habit habit) {
        if (ecoNews != null) {
            Tag ecoNewsTag = ModelUtils.getTag();
            ecoNewsTag.setId(1L);
            ecoNews.setTags(List.of(ecoNewsTag));
        }

        if (event != null) {
            Tag eventTag = ModelUtils.getEventTag();
            eventTag.setId(6L);
            event.setTags(List.of(eventTag));
        }

        if (habit != null) {
            Tag habitTag = ModelUtils.getHabitTag();
            habitTag.setId(9L);
            habit.setTags(Set.of(habitTag));
        }
    }

    private void prepareEmptyTagsCollections(EcoNews ecoNews, Event event, Habit habit) {
        if (ecoNews != null) {
            ecoNews.setTags(List.of());
        }

        if (event != null) {
            event.setTags(List.of());
        }

        if (habit != null) {
            habit.setTags(Set.of());
        }
    }
}
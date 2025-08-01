package greencity.dto.relevance;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import greencity.dto.cache.CachedUserRelevanceProfile;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsRelevance;
import greencity.entity.Tag;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

class EcoNewsWithRelevanceVectorsDtoTest {
    @Test
    void constructorSetsEcoNewsTagsVectorAndTitleVector() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tag2 = new Tag();
        tag2.setId(2L);
        List<Tag> tags = List.of(tag1, tag2);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        Float[] titleVector = new Float[] {0.1f, 0.2f};

        EcoNews ecoNews = mock(EcoNews.class);
        EcoNewsRelevance ecoNewsRelevance = mock(EcoNewsRelevance.class);

        when(ecoNews.getTags()).thenReturn(tags);
        when(ecoNewsRelevance.getTitleVector()).thenReturn(titleVector);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNews, ecoNewsRelevance, tagsIndexes);

        assertSame(ecoNews, dto.getEcoNews());
        assertArrayEquals(new Float[] {1.0f, 1.0f}, dto.getTagsVector());
        assertArrayEquals(titleVector, dto.getTitleVector());
        assertEquals(0.0, dto.getRelevanceScore());
    }

    @Test
    void constructorWithNullEcoNewsRelevance() {
        EcoNews ecoNews = mock(EcoNews.class);
        Map<Long, Integer> tagsIndexes = Map.of();

        when(ecoNews.getTags()).thenReturn(Collections.emptyList());

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNews, null, tagsIndexes);
        assertNull(dto.getTitleVector());
    }

    @Test
    void constructorCalculatesRelevanceScoreWithBothVectors() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tag2 = new Tag();
        tag2.setId(2L);
        List<Tag> tags = List.of(tag1, tag2);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        Float[] titleVector = new Float[] {1.0f, 0.0f};
        Float[] userTagsPreferences = new Float[] {1.0f, null};
        Float[] userTitlePreferences = new Float[] {1.0f, 0.0f};
        double[] weights = new double[] {0.5, 0.5};

        EcoNews ecoNews = mock(EcoNews.class);
        EcoNewsRelevance ecoNewsRelevance = mock(EcoNewsRelevance.class);
        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);

        when(ecoNews.getTags()).thenReturn(tags);
        when(ecoNewsRelevance.getTitleVector()).thenReturn(titleVector);
        when(userProfile.tagsPreferencesVector()).thenReturn(userTagsPreferences);
        when(userProfile.titlePreferencesVector()).thenReturn(userTitlePreferences);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNews, ecoNewsRelevance, tagsIndexes, userProfile, weights);

        assertEquals(1.0, dto.getRelevanceScore(), 1e-2);
    }

    @Test
    void constructorCalculatesRelevanceScoreOnlyTagsVector() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        List<Tag> tags = List.of(tag1);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0);
        Float[] userTagsPreferences = new Float[] {1.0f};
        double[] weights = new double[] {1.0, 1.0};

        EcoNews ecoNews = mock(EcoNews.class);
        EcoNewsRelevance ecoNewsRelevance = mock(EcoNewsRelevance.class);
        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);

        when(ecoNews.getTags()).thenReturn(tags);
        when(ecoNewsRelevance.getTitleVector()).thenReturn(null);
        when(userProfile.tagsPreferencesVector()).thenReturn(userTagsPreferences);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNews, ecoNewsRelevance, tagsIndexes, userProfile, weights);

        assertEquals(1.0, dto.getRelevanceScore(), 1e-2);
    }

    @Test
    void constructorWithBothEmptyVectors() {
        Map<Long, Integer> tagsIndexes = Map.of();
        Float[] userPreferencesVector = new Float[] {};
        Float[] titleVector = new Float[] {1.0f, 0.0f};
        double[] weights = new double[] {1.0, 1.0};

        EcoNews ecoNews = mock(EcoNews.class);
        EcoNewsRelevance ecoNewsRelevance = mock(EcoNewsRelevance.class);
        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);

        when(ecoNews.getTags()).thenReturn(Collections.emptyList());
        when(ecoNewsRelevance.getTitleVector()).thenReturn(titleVector);
        when(userProfile.tagsPreferencesVector()).thenReturn(userPreferencesVector);
        when(userProfile.titlePreferencesVector()).thenReturn(userPreferencesVector);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNews, ecoNewsRelevance, tagsIndexes, userProfile, weights);

        assertEquals(0.0, dto.getRelevanceScore());
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("provideInvalidVectors")
    void constructorWithInvalidVectors(Float[] invalidVector) {
        Map<Long, Integer> tagsIndexes = Map.of();
        Float[] userPreferencesVector = new Float[] {};
        double[] weights = new double[] {1.0, 1.0};

        EcoNews ecoNews = mock(EcoNews.class);
        EcoNewsRelevance ecoNewsRelevance = mock(EcoNewsRelevance.class);
        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);

        when(ecoNews.getTags()).thenReturn(Collections.emptyList());
        when(ecoNewsRelevance.getTitleVector()).thenReturn(invalidVector);
        when(userProfile.tagsPreferencesVector()).thenReturn(userPreferencesVector);
        when(userProfile.titlePreferencesVector()).thenReturn(userPreferencesVector);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNews, ecoNewsRelevance, tagsIndexes, userProfile, weights);

        assertEquals(0.0, dto.getRelevanceScore());
    }

    static Stream<Arguments> provideInvalidVectors() {
        return Stream.of(
            Arguments.of((Object) new Float[] {}),
            Arguments.of((Object) new Float[] {null, null}));
    }

}
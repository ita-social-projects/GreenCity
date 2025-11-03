package greencity.dto.relevance;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import greencity.dto.cache.CachedUserRelevanceProfile;
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
        Long ecoNewsId = 1L;
        List<Long> tagIds = List.of(1L, 2L);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        Float[] titleVector = new Float[] {0.1f, 0.2f};

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNewsId, tagIds, titleVector, tagsIndexes);

        assertEquals(ecoNewsId, dto.getEcoNewsId());
        assertArrayEquals(new Float[] {1.0f, 1.0f}, dto.getTagsVector());
        assertArrayEquals(titleVector, dto.getTitleVector());
        assertEquals(0.0, dto.getRelevanceScore());
    }

    @Test
    void constructorWithNullEcoNewsRelevance() {
        Long ecoNewsId = 1L;
        List<Long> tagIds = Collections.emptyList();
        Map<Long, Integer> tagsIndexes = Map.of();

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNewsId, tagIds, null, tagsIndexes);

        assertEquals(ecoNewsId, dto.getEcoNewsId());
        assertNull(dto.getTitleVector());
        assertEquals(0.0, dto.getRelevanceScore());
    }

    @Test
    void constructorCalculatesRelevanceScoreWithBothVectors() {
        Long ecoNewsId = 1L;
        List<Long> tagIds = List.of(1L, 2L);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        Float[] titleVector = new Float[] {1.0f, 0.0f};
        Float[] userTagsPreferences = new Float[] {1.0f, 1.0f};
        Float[] userTitlePreferences = new Float[] {1.0f, 0.0f};
        double[] weights = new double[] {0.5, 0.5};

        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);
        when(userProfile.tagsPreferencesVector()).thenReturn(userTagsPreferences);
        when(userProfile.titlePreferencesVector()).thenReturn(userTitlePreferences);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNewsId, tagIds, titleVector, tagsIndexes, userProfile, weights);

        assertEquals(ecoNewsId, dto.getEcoNewsId());
        assertEquals(1.0, dto.getRelevanceScore(), 1e-2);
    }

    @Test
    void constructorCalculatesRelevanceScoreOnlyTagsVector() {
        Long ecoNewsId = 1L;
        List<Long> tagIds = List.of(1L);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0);
        Float[] userTagsPreferences = new Float[] {1.0f};
        double[] weights = new double[] {1.0, 1.0};

        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);
        when(userProfile.tagsPreferencesVector()).thenReturn(userTagsPreferences);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNewsId, tagIds, null, tagsIndexes, userProfile, weights);

        assertEquals(ecoNewsId, dto.getEcoNewsId());
        assertEquals(1.0, dto.getRelevanceScore(), 1e-2);
    }

    @Test
    void constructorWithBothEmptyVectors() {
        Long ecoNewsId = 1L;
        List<Long> tagIds = Collections.emptyList(); // This will create a zero vector
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        Float[] titleVector = new Float[] {1.0f, 0.0f};
        Float[] userTagsPreferences = new Float[] {1.0f, 1.0f};
        Float[] userTitlePreferences = new Float[] {1.0f, 0.0f};
        double[] weights = new double[] {0.5, 0.5};

        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);
        when(userProfile.tagsPreferencesVector()).thenReturn(userTagsPreferences);
        when(userProfile.titlePreferencesVector()).thenReturn(userTitlePreferences);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNewsId, tagIds, titleVector, tagsIndexes, userProfile, weights);

        assertEquals(ecoNewsId, dto.getEcoNewsId());
        assertEquals(0.5, dto.getRelevanceScore(), 1e-2);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("provideInvalidVectors")
    void constructorWithInvalidVectors(Float[] invalidVector) {
        Long ecoNewsId = 1L;
        List<Long> tagIds = Collections.emptyList();
        Map<Long, Integer> tagsIndexes = Map.of();
        Float[] userTagsPreferences = new Float[] {};
        Float[] userTitlePreferences = new Float[] {};
        double[] weights = new double[] {1.0, 1.0};

        CachedUserRelevanceProfile userProfile = mock(CachedUserRelevanceProfile.class);
        when(userProfile.tagsPreferencesVector()).thenReturn(userTagsPreferences);
        when(userProfile.titlePreferencesVector()).thenReturn(userTitlePreferences);

        EcoNewsWithRelevanceVectorsDto dto = new EcoNewsWithRelevanceVectorsDto(
            ecoNewsId, tagIds, invalidVector, tagsIndexes, userProfile, weights);

        assertEquals(ecoNewsId, dto.getEcoNewsId());
        assertEquals(0.0, dto.getRelevanceScore());
    }

    static Stream<Arguments> provideInvalidVectors() {
        return Stream.of(
            Arguments.of((Object) new Float[] {}),
            Arguments.of((Object) new Float[] {null, null}));
    }

}
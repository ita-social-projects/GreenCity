package greencity.dto.relevance;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EntityWithTagsVectorTest {
    @Test
    void constructorWithNullTags() {
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);

        TestEntityWithTagsVector entity = new TestEntityWithTagsVector(null, tagsIndexes);

        assertNotNull(entity.getTagsVector());
        assertEquals(2, entity.getTagsVector().length);
        assertEquals(0.0f, entity.getTagsVector()[0]);
        assertEquals(0.0f, entity.getTagsVector()[1]);
    }

    @Test
    void constructorWithTags() {
        List<Long> tagIds = List.of(1L, 2L);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);

        TestEntityWithTagsVector entity = new TestEntityWithTagsVector(tagIds, tagsIndexes);

        assertArrayEquals(new Float[] {1.0f, 1.0f}, entity.getTagsVector());
    }

    @Test
    void constructorWithPartialTags() {
        List<Long> tagIds = List.of(1L);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);

        TestEntityWithTagsVector entity = new TestEntityWithTagsVector(tagIds, tagsIndexes);

        assertEquals(1.0f, entity.getTagsVector()[0]);
        assertEquals(0.0f, entity.getTagsVector()[1]);
    }

    @Test
    void constructorWithNonIndexedTagId() {
        List<Long> tagIds = List.of(1L, 999L);
        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);

        TestEntityWithTagsVector entity =
            assertDoesNotThrow(() -> new TestEntityWithTagsVector(tagIds, tagsIndexes));
        assertArrayEquals(new Float[] {1.0f, 0.0f}, entity.getTagsVector());
    }

    @Test
    void constructorWithEmptyTagsIndexes() {
        List<Long> tagIds = new ArrayList<>();
        Map<Long, Integer> tagsIndexes = new HashMap<>();
        TestEntityWithTagsVector entity = new TestEntityWithTagsVector(tagIds, tagsIndexes);

        assertNotNull(entity.getTagsVector());
        assertEquals(0, entity.getTagsVector().length);
    }

    private static class TestEntityWithTagsVector extends EntityWithTagsVector {
        public TestEntityWithTagsVector(Collection<Long> tagIds, Map<Long, Integer> tagsIndexes) {
            super(tagIds, tagsIndexes);
        }
    }
}
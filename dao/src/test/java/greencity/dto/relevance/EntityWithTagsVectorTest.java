package greencity.dto.relevance;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import greencity.entity.Tag;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tag2 = new Tag();
        tag2.setId(2L);

        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        List<Tag> tags = Arrays.asList(tag1, tag2);

        TestEntityWithTagsVector entity = new TestEntityWithTagsVector(tags, tagsIndexes);

        assertArrayEquals(new Float[] {1.0f, 1.0f}, entity.getTagsVector());
    }

    @Test
    void constructorWithPartialTags() {
        Tag tag1 = new Tag();
        tag1.setId(1L);

        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        List<Tag> tags = Collections.singletonList(tag1);

        TestEntityWithTagsVector entity = new TestEntityWithTagsVector(tags, tagsIndexes);

        assertEquals(1.0f, entity.getTagsVector()[0]);
        assertEquals(0.0f, entity.getTagsVector()[1]);
    }

    @Test
    void constructorWithNonIndexedTagId() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        Tag tagUnknown = new Tag();
        tagUnknown.setId(999L);

        Map<Long, Integer> tagsIndexes = Map.of(1L, 0, 2L, 1);
        List<Tag> tags = Arrays.asList(tag1, tagUnknown);

        TestEntityWithTagsVector entity =
            assertDoesNotThrow(() -> new TestEntityWithTagsVector(tags, tagsIndexes));
        assertArrayEquals(new Float[] {1.0f, 0.0f}, entity.getTagsVector());
    }

    @Test
    void constructorWithEmptyTagsIndexes() {
        List<Tag> tags = new ArrayList<>();
        Map<Long, Integer> tagsIndexes = new HashMap<>();
        TestEntityWithTagsVector entity = new TestEntityWithTagsVector(tags, tagsIndexes);

        assertNotNull(entity.getTagsVector());
        assertEquals(0, entity.getTagsVector().length);
    }

    private static class TestEntityWithTagsVector extends EntityWithTagsVector {
        public TestEntityWithTagsVector(Collection<Tag> tags, Map<Long, Integer> tagsIndexes) {
            super(tags, tagsIndexes);
        }
    }
}
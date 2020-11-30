/*package greencity.repository;

import greencity.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/tags.sql")
class TagsRepoTest {
    @Autowired
    TagsRepo tagsRepo;

    @Test
    void findEcoNewsTagsByNamesTest() {
        List<String> ecoNewsTagNames = Arrays.asList("news", "education");
        List<Tag> tags = tagsRepo.findEcoNewsTagsByNames(ecoNewsTagNames);
        assertEquals(2, tags.size());
    }

    @Test
    void findEcoNewsTagsByNamesNotFoundTest() {
        List<String> ecoNewsTagNames = Collections.singletonList("xxx");
        List<Tag> tags = tagsRepo.findEcoNewsTagsByNames(ecoNewsTagNames);
        assertTrue(tags.isEmpty());
    }

    @Test
    void findTipsAndTricksTagsByNamesTest() {
        List<String> tipsAndTricksTagNames = Arrays.asList("news", "education");
        List<Tag> tags = tagsRepo.findTipsAndTricksTagsByNames(tipsAndTricksTagNames);
        assertEquals(2, tags.size());
    }

    @Test
    void findTipsAndTricksTagsByNamesNotFoundTest() {
        List<String> tipsAndTricksTagNames = Collections.singletonList("xxx");
        List<Tag> tags = tagsRepo.findTipsAndTricksTagsByNames(tipsAndTricksTagNames);
        assertTrue(tags.isEmpty());
    }

    @Test
    void findAllEcoNewsTagsTest() {
        List<String> ecoNewsTags = tagsRepo.findAllEcoNewsTags();
        assertEquals(3, ecoNewsTags.size());
    }

    @Test
    void findAllTipsAndTricksTagsTest() {
        List<String> tipsAndTricksTags = tagsRepo.findAllTipsAndTricksTags();
        assertEquals(1, tipsAndTricksTags.size());
    }

}*/
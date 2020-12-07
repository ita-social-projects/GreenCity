package greencity.repository;

import greencity.entity.Tag;
import greencity.enums.TagType;
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

    private static final String UKRAINIAN_LANGUAGE = "ua";
    private static final String ENGLISH_LANGUAGE = "en";

    @Test
    void findTagsByNamesTest() {
        List<String> tagNames = Collections.singletonList("news");
        List<Tag> tags = tagsRepo.findTagsByNamesAndType(tagNames, TagType.ECO_NEWS);
        assertEquals(1, tags.size());
    }

    @Test
    void findByTypeAndLanguageCode() {
        String tagType = "ECO_NEWS";
        String languageCode = "en";
        List<String> expected = Collections.singletonList("News");
        List<String> actual =  tagsRepo.findTagsByTypeAndLanguageCode(tagType, languageCode);

        assertEquals(expected, actual);
    }

    @Test
    void findTagsByNamesNotFoundTest() {
        List<String> tagNames = Collections.singletonList("news");
        List<Tag> tags = tagsRepo.findTagsByNamesAndType(tagNames, TagType.TIPS_AND_TRICKS);
        assertTrue(tags.isEmpty());
    }

    @Test
    void findAllTipsAndTricksTagsWithEnglishTest() {
        List<String> actual = tagsRepo.findAllTipsAndTricksTags(ENGLISH_LANGUAGE);
        List<String> expected = Collections.singletonList("Ads");
        assertEquals(1, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void findAllTipsAndTricksTagsWithUkrainianTest() {
        List<String> actual = tagsRepo.findAllTipsAndTricksTags(UKRAINIAN_LANGUAGE);
        List<String> expected = Collections.singletonList("Реклами");
        assertEquals(1, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void findAllEcoNewsTagsWithEnglishTest() {
        List<String> actual = tagsRepo.findAllEcoNewsTags(ENGLISH_LANGUAGE);
        List<String> expected = Arrays.asList("Ads", "Education", "News");
        assertEquals(3, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void findAllEcoNewsTagsWithUkrainianTest() {
        List<String> actual = tagsRepo.findAllEcoNewsTags(UKRAINIAN_LANGUAGE);
        List<String> expected = Arrays.asList("Новини", "Освіта", "Реклами");
        assertEquals(3, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void findAllHabitsTagsWithEnglishTest() {
        List<String> actual = tagsRepo.findAllHabitsTags(ENGLISH_LANGUAGE);
        List<String> expected = Arrays.asList("Ads", "Education", "News");
        assertEquals(3, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void findAllHabitsTagsWithUkrainianTest() {
        List<String> actual = tagsRepo.findAllHabitsTags(UKRAINIAN_LANGUAGE);
        List<String> expected = Arrays.asList("Новини", "Освіта", "Реклами");
        assertEquals(3, actual.size());
        assertEquals(expected, actual);
    }
}
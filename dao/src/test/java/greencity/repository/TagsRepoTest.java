package greencity.repository;

import greencity.ModelUtils;
import greencity.entity.Tag;
import greencity.enums.TagType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Sql("classpath:sql/tags.sql")
class TagsRepoTest {
    @Autowired
    TagsRepo tagsRepo;

    @Autowired
    TagTranslationRepo tagTranslationRepo;

    private static final String UKRAINIAN_LANGUAGE = "ua";
    private static final String ENGLISH_LANGUAGE = "en";

    private static List<Long> convertTagListToLongList(List<Tag> tags) {
        return tags.stream().map(Tag::getId).collect(Collectors.toList());
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 9);
        List<Tag> expectedList = Arrays.asList(ModelUtils.getTagEcoNews(), ModelUtils.getTagHabit(),
            ModelUtils.getTagTipsAndTricks());

        Page<Tag> expected = new PageImpl<>(expectedList, pageable, expectedList.size());
        Page<Tag> actual = tagsRepo.findAll(pageable);

        List<Long> expectedIds = convertTagListToLongList(expected.getContent());
        List<Long> actualIds = convertTagListToLongList(actual.getContent());

        assertEquals(expectedIds, actualIds);
    }

    @Test
    void findById() {
        Long id = 1L;
        Optional<Tag> expected = Optional.of(ModelUtils.getTagEcoNews());
        Optional<Tag> actual = tagsRepo.findById(id);

        assertEquals(expected.get().getId(), actual.get().getId());
    }

    @Test
    void filterByAllFields() {
        String filter = "News";
        Pageable pageable = PageRequest.of(0, 1);
        List<Tag> expectedList = Collections.singletonList(ModelUtils.getTagEcoNews());

        Page<Tag> expected = new PageImpl<>(expectedList, pageable, expectedList.size());
        Page<Tag> actual = tagsRepo.filterByAllFields(pageable, filter);

        List<Long> expectedIds = convertTagListToLongList(expected.getContent());
        List<Long> actualIds = convertTagListToLongList(actual.getContent());

        assertEquals(expectedIds, actualIds);
    }

    @Test
    void bulkDelete() {
        List<Long> ids = Arrays.asList(1L, 2L);

        tagTranslationRepo.bulkDeleteByTagId(ids);
        tagsRepo.bulkDelete(ids);

        assertEquals(1, tagsRepo.findAll().size());
    }

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
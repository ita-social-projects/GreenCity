package greencity.service.impl;

import greencity.ModelUtils;
import greencity.entity.Tag;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TagsRepo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(SpringExtension.class)
class TagsServiceImplTest {
    @Mock
    private TagsRepo tagRepo;

    @InjectMocks
    private TagsServiceImpl tagService;

    @Test
    void testFindAll() {
        Tag tag = ModelUtils.getTag();
        tag.setEcoNews(Collections.singletonList(ModelUtils.getEcoNews()));
        when(tagRepo.findAllEcoNewsTags()).thenReturn(Collections.singletonList(tag.getName()));
        List<String> expected = Collections.singletonList(tag.getName());
        assertEquals(expected, tagService.findAllEcoNewsTags());
    }

    @Test
    void testFindAllWithoutEcoNews() {
        when(tagRepo.findAllEcoNewsTags()).thenReturn(Collections.singletonList(ModelUtils.getTag().getName()));
        List<String> expected = Collections.singletonList(ModelUtils.getTag().getName());
        assertEquals(expected, tagService.findAllEcoNewsTags());
    }

    @Test
    void findAllTest() {
        Tag tipsAndTricksTag = ModelUtils.getTag();
        tipsAndTricksTag.setTipsAndTricks(Collections.singletonList(ModelUtils.getTipsAndTricks()));
        when(tagRepo.findAllTipsAndTricksTags()).thenReturn(Collections.singletonList(tipsAndTricksTag.getName()));
        List<String> expected = Collections.singletonList(tipsAndTricksTag.getName());
        assertEquals(expected, tagService.findAllTipsAndTricksTags());
    }

    @Test
    void findAllWithoutTipsAndTricksTest() {
        when(tagRepo.findAllTipsAndTricksTags()).thenReturn(Collections.singletonList(ModelUtils.getTag().getName()));
        List<String> expected = Collections.singletonList(ModelUtils.getTag().getName());
        assertEquals(expected, tagService.findAllTipsAndTricksTags());
    }

    @Test
    void findAllByNamesTest() {
        Tag tipsAndTricksTag = ModelUtils.getTag();
        List<Tag> expected = Collections.singletonList(tipsAndTricksTag);
        when(tagRepo.findTipsAndTricksTagsByNames(Collections.singletonList(tipsAndTricksTag.getName())))
            .thenReturn(expected);
        assertEquals(expected,
            tagService.findTipsAndTricksTagsByNames(Collections.singletonList(tipsAndTricksTag.getName())));
    }

    @Test
    void findAllByNamesFailedTest() {
        String name = ModelUtils.getTag().getName();
        List<String> tipsAndTricksTagsNames = Collections.singletonList(name);
        when(tagRepo.findTipsAndTricksTagsByNames(tipsAndTricksTagsNames)).thenReturn(Collections.emptyList());
        assertThrows(TagNotFoundException.class, () -> tagService.findTipsAndTricksTagsByNames(tipsAndTricksTagsNames));
    }

    @Test
    void isAllValidTrueTest() {
        String name = ModelUtils.getTag().getName();
        List<String> tipsAndTricksTagsNames = Collections.singletonList(name);
        when(tagRepo.findTipsAndTricksTagsByNames(tipsAndTricksTagsNames))
            .thenReturn(Collections.singletonList(ModelUtils.getTag()));
        assertEquals(true, tagService.isAllTipsAndTricksValid(tipsAndTricksTagsNames));
    }

    @Test
    void isAllValidFalseTest() {
        String name = ModelUtils.getTag().getName();
        List<String> tipsAndTricksTagsNames = Collections.singletonList(name);
        when(tagRepo.findTipsAndTricksTagsByNames(tipsAndTricksTagsNames))
            .thenReturn(Collections.emptyList());
        assertEquals(false, tagService.isAllTipsAndTricksValid(tipsAndTricksTagsNames));
    }

    @Test
    void isValidNumOfUniqueTagsTrueTest() {
        String name = ModelUtils.getTag().getName();
        assertEquals(true, tagService.isValidNumOfUniqueTags(Collections.singletonList(name)));
    }

    @Test
    void isValidNumOfUniqueTagsDuplicatedTagExceptionTest() {
        String name = ModelUtils.getTag().getName();
        List<String> tipsAndTricksTagsNames = Arrays.asList(name, name);
        assertThrows(DuplicatedTagException.class, () -> tagService.isValidNumOfUniqueTags(tipsAndTricksTagsNames));
    }

    @Test
    void isValidNumOfUniqueTagsInvalidNumOfTagsException() {
        List<String> tipsAndTricksTagsNames = Arrays.asList("name1", "name2", "name3", "name4");
        assertThrows(InvalidNumOfTagsException.class, () -> tagService.isValidNumOfUniqueTags(tipsAndTricksTagsNames));
    }
}

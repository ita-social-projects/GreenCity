package greencity.service;

import greencity.ModelUtils;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TagsRepo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TagsServiceImplTest {
    @Mock
    private TagsRepo tagRepo;

    @Mock
    private ModelMapper modelMapper;

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
        List<TagVO> tagVOList = Collections.singletonList(new TagVO(1L, "tag", null, null));
        when(modelMapper.map(expected, new TypeToken<List<TagVO>>() {
        }.getType())).thenReturn(tagVOList);
        assertEquals(tagVOList,
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

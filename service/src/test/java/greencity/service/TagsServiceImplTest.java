package greencity.service;

import greencity.ModelUtils;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.enums.TagType;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TagsRepo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
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
    private TagsServiceImpl tagsService;

    private static final String UKRAINIAN_LANGUAGE = "ua";
    private static final String ENGLISH_LANGUAGE = "en";

    @Test
    void findTagsByNames() {
        TagType tagType = TagType.ECO_NEWS;
        List<String> tagsNames = Collections.singletonList("News");
        List<String> lowerTagsNames = tagsNames.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        List<Tag> ecoNewsTags = ModelUtils.getTags();
        List<TagVO> actual = Collections.singletonList(ModelUtils.getTagVO());
        when(tagRepo.findTagsByNamesAndType(lowerTagsNames, tagType)).thenReturn(ecoNewsTags);
        when(modelMapper.map(ecoNewsTags, new TypeToken<List<TagVO>>() {
        }.getType())).thenReturn(actual);
        List<TagVO> expected = tagsService.findTagsByNamesAndType(tagsNames, tagType);

        assertEquals(expected, actual);
    }

    @Test
    void findTagsByNamesThrowTagNotFoundException() {
        TagType tagType = TagType.TIPS_AND_TRICKS;
        List<String> tagsNames = Collections.singletonList("News");
        List<String> lowerTagsNames = tagsNames.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
        List<Tag> tags = Collections.emptyList();
        when(tagRepo.findTagsByNamesAndType(lowerTagsNames, tagType)).thenReturn(tags);

        assertThrows(TagNotFoundException.class,
            () -> tagsService.findTagsByNamesAndType(tagsNames, tagType));
    }

    @Test
    void findAllEcoNewsTags() {
        List<String> actual = Collections.singletonList("News");
        when(tagRepo.findAllEcoNewsTags(ENGLISH_LANGUAGE)).thenReturn(actual);
        List<String> expected = tagsService.findAllEcoNewsTags(ENGLISH_LANGUAGE);

        assertEquals(expected, actual);
    }

    @Test
    void findAllTipsAndTricksTags() {
        List<String> actual = Collections.singletonList("Новини");
        when(tagRepo.findAllTipsAndTricksTags(UKRAINIAN_LANGUAGE)).thenReturn(actual);
        List<String> expected = tagsService.findAllTipsAndTricksTags(UKRAINIAN_LANGUAGE);

        assertEquals(expected, actual);
    }

    @Test
    void findAllHabitsTags() {
        List<String> actual = Collections.singletonList("Новини");
        when(tagRepo.findAllHabitsTags(UKRAINIAN_LANGUAGE)).thenReturn(actual);
        List<String> expected = tagsService.findAllHabitsTags(UKRAINIAN_LANGUAGE);

        assertEquals(expected, actual);
    }

    @Test
    void isAllTipsAndTricksValidReturnFalse() {
        TagType tagType = TagType.TIPS_AND_TRICKS;
        List<String> tipsAndTricksTagsNames = Collections.singletonList("News");
        when(tagRepo.findTagsByNamesAndType(tipsAndTricksTagsNames, tagType)).thenThrow(TagNotFoundException.class);
        boolean expected = tagsService.isAllTipsAndTricksValid(tipsAndTricksTagsNames, tagType);

        assertFalse(expected);
    }

    @Test
    void isValidNumOfUniqueTagsReturnTrue() {
        List<String> tagNames = Arrays.asList("News", "Education");
        boolean expected = tagsService.isValidNumOfUniqueTags(tagNames);

        assertTrue(expected);
    }

    @Test
    void isValidNumOfUniqueTagsThrowsDuplicatedTagsException() {
        List<String> tagNames = Arrays.asList("News", "News");

        assertThrows(DuplicatedTagException.class,
            () -> tagsService.isValidNumOfUniqueTags(tagNames));
    }

    @Test
    void isValidNumOfUniqueTagsThrowsInvalidNumOfTagsException() {
        List<String> tagNames = Arrays.asList("News", "Education", "Ads", "Events");

        assertThrows(InvalidNumOfTagsException.class,
            () -> tagsService.isValidNumOfUniqueTags(tagNames));
    }
}

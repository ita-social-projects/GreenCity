package greencity.service.impl;

import greencity.ModelUtils;
import greencity.entity.TipsAndTricksTag;
import greencity.exception.exceptions.DuplicatedTagException;
import greencity.exception.exceptions.InvalidNumOfTagsException;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.TipsAndTricksTagsRepo;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.powermock.api.mockito.PowerMockito.when;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class TipsAndTricksTagsServiceImplTest {
    @Mock
    private TipsAndTricksTagsRepo tipsAndTricksTagsRepo;

    @InjectMocks
    private TipsAndTricksTagsServiceImpl tipsAndTricksTagsService;

    @Test
    public void findAllTest() {
        TipsAndTricksTag tipsAndTricksTag = ModelUtils.getTipsAndTricksTag();
        tipsAndTricksTag.setTipsAndTricks(Collections.singletonList(ModelUtils.getTipsAndTricks()));
        when(tipsAndTricksTagsRepo.findAll()).thenReturn(Collections.singletonList(tipsAndTricksTag));
        List<String> expected = Collections.singletonList(tipsAndTricksTag.getName());
        assertEquals(expected, tipsAndTricksTagsService.findAll());
    }

    @Test
    public void findAllWithoutTipsAndTricksTest() {
        when(tipsAndTricksTagsRepo.findAll()).thenReturn(Collections.singletonList(ModelUtils.getTipsAndTricksTag()));
        List<String> expected = Collections.emptyList();
        assertEquals(expected, tipsAndTricksTagsService.findAll());
    }

    @Test
    public void findByNameTest() {
        TipsAndTricksTag expected = ModelUtils.getTipsAndTricksTag();
        when(tipsAndTricksTagsRepo.findByName(expected.getName())).thenReturn(Optional.of(expected));
        assertEquals(expected, tipsAndTricksTagsService.findByName(expected.getName()));
    }

    @Test
    public void findByNameFailedTest() {
        String name = ModelUtils.getTipsAndTricksTag().getName();
        when(tipsAndTricksTagsRepo.findByName(name)).thenReturn(Optional.empty());
        assertThrows(TagNotFoundException.class, () -> {
            tipsAndTricksTagsService.findByName(name);
        });
    }

    @Test
    public void findAllByNamesTest() {
        TipsAndTricksTag tipsAndTricksTag = ModelUtils.getTipsAndTricksTag();
        List<TipsAndTricksTag> expected = Collections.singletonList(tipsAndTricksTag);
        when(tipsAndTricksTagsRepo.findAllByNames(Collections.singletonList(tipsAndTricksTag.getName())))
            .thenReturn(expected);
        assertEquals(expected,
            tipsAndTricksTagsService.findAllByNames(Collections.singletonList(tipsAndTricksTag.getName())));
    }

    @Test
    public void findAllByNamesFailedTest() {
        String name = ModelUtils.getTipsAndTricksTag().getName();
        when(tipsAndTricksTagsRepo.findAllByNames(Collections.singletonList(name))).thenReturn(Collections.emptyList());
        assertThrows(TagNotFoundException.class, () -> {
            tipsAndTricksTagsService.findAllByNames(Collections.singletonList(name));
        });
    }

    @Test
    public void isAllValidTrueTest() {
        String name = ModelUtils.getTipsAndTricksTag().getName();
        List<String> tipsAndTricksTagsNames = Collections.singletonList(name);
        when(tipsAndTricksTagsRepo.findAllByNames(tipsAndTricksTagsNames))
            .thenReturn(Collections.singletonList(ModelUtils.getTipsAndTricksTag()));
        assertEquals(true, tipsAndTricksTagsService.isAllValid(tipsAndTricksTagsNames));
    }

    @Test
    public void isAllValidFalseTest() {
        String name = ModelUtils.getTipsAndTricksTag().getName();
        List<String> tipsAndTricksTagsNames = Collections.singletonList(name);
        when(tipsAndTricksTagsRepo.findAllByNames(tipsAndTricksTagsNames))
            .thenReturn(Collections.emptyList());
        assertEquals(false, tipsAndTricksTagsService.isAllValid(tipsAndTricksTagsNames));
    }

    @Test
    public void isValidNumOfUniqueTagsTrueTest() {
        String name = ModelUtils.getTipsAndTricksTag().getName();
        assertEquals(true, tipsAndTricksTagsService.isValidNumOfUniqueTags(Collections.singletonList(name)));
    }

    @Test
    public void isValidNumOfUniqueTagsDuplicatedTagExceptionTest() {
        String name = ModelUtils.getTipsAndTricksTag().getName();
        assertThrows(DuplicatedTagException.class, () -> {
            tipsAndTricksTagsService.isValidNumOfUniqueTags(Arrays.asList(name, name));
        });
    }

    @Test
    public void isValidNumOfUniqueTagsInvalidNumOfTagsException() {
        assertThrows(InvalidNumOfTagsException.class, () -> {
            tipsAndTricksTagsService.isValidNumOfUniqueTags(Arrays.asList("name1", "name2", "name3", "name4"));
        });
    }
}

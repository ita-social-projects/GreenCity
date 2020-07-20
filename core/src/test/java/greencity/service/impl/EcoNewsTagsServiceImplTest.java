package greencity.service.impl;

import greencity.ModelUtils;
import greencity.entity.Tag;
import greencity.exception.exceptions.TagNotFoundException;
import greencity.repository.EcoNewsTagsRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(SpringExtension.class)
public class EcoNewsTagsServiceImplTest {
    @Mock
    private EcoNewsTagsRepo tagRepo;

    @InjectMocks
    private EcoNewsTagsServiceImpl tagService;

    @Test
    public void testFindByName() {
        Tag expected = ModelUtils.getTag();
        when(tagRepo.findByNameIgnoreCase(expected.getName()))
            .thenReturn(Optional.of(expected));

        assertEquals(expected, tagService.findByName(expected.getName()));
    }

    @Test
    public void testFindByNameWithException() {
        String name = ModelUtils.getTag().getName();
        when(tagRepo.findByNameIgnoreCase(name)).thenReturn(Optional.empty());

        assertThrows(TagNotFoundException.class, () -> tagService.findByName(name));
    }

    @Test
    public void testFindAll() {
        Tag tag = ModelUtils.getTag();
        tag.setEcoNews(Collections.singletonList(ModelUtils.getEcoNews()));
        when(tagRepo.findAll()).thenReturn(Collections.singletonList(tag));

        List<String> expected = Collections.singletonList(tag.getName());

        assertEquals(expected, tagService.findAll());
    }

    @Test
    public void testFindAllWithoutEcoNews() {
        when(tagRepo.findAll()).thenReturn(Collections.singletonList(ModelUtils.getTag()));

        List<String> expected = Collections.emptyList();

        assertEquals(expected, tagService.findAll());
    }
}

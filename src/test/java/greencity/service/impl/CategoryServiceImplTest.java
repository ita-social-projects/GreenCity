package greencity.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import greencity.GreenCityApplication;
import greencity.entity.Category;
import greencity.exception.NotFoundException;
import greencity.repository.CategoryRepo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepo categoryRepo;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void saveTest() {
        Category genericEntity = new Category();

        when(categoryRepo.save(genericEntity)).thenReturn(genericEntity);

        assertEquals(genericEntity, categoryService.save(genericEntity));
    }

    @Test
    public void findByIdTest() {
        Category genericEntity = new Category();

        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));

        Category foundEntity = categoryService.findById(anyLong());

        assertEquals(genericEntity, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdGivenIdNullThenThrowException() {
        categoryService.findById(null);
    }

    @Test
    public void updateTest() {
        Category updated = new Category();

        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(updated));
        when(categoryRepo.save(any())).thenReturn(updated);

        categoryService.update(anyLong(), updated);
        Category foundEntity = categoryService.findById(anyLong());

        assertEquals(updated, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void updateGivenIdNullThenThrowException() {
        categoryService.update(null, new Category());
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdGivenIdNullThenThrowException() {
        categoryService.deleteById(null);
    }

    @Test
    public void findAllTest() {
        List<Category> genericEntities =
            new ArrayList<>(Arrays.asList(new Category(), new Category()));

        when(categoryRepo.findAll()).thenReturn(genericEntities);

        List<Category> foundEntities = categoryService.findAll();

        assertEquals(genericEntities, foundEntities);
    }
}

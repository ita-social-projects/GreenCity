package greencity.service.impl;

import greencity.GreenCityApplication;
import greencity.entity.Category;
import greencity.exception.NotFoundException;
import greencity.repository.CategoryRepo;
import greencity.service.CategoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
public class CategoryServiceImplTest {
    @MockBean private CategoryRepo categoryRepo;
    @Autowired private CategoryService categoryService;

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
    public void deleteByIdThrowExceptionWhenCallFindById() {
        Category generic = new Category();

        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(generic));
        when(categoryService.findById(anyLong())).thenThrow(NotFoundException.class);

        categoryService.deleteById(1L);
        categoryService.findById(1L);
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

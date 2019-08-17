package greencity.services.impl;

import static org.junit.Assert.*;

import greencity.GreenCityApplication;
import greencity.entities.Category;
import greencity.exceptions.NotFoundException;
import greencity.services.CategoryService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GreenCityApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryServiceImplTest {
    @Autowired private CategoryService categoryService;

    @Test
    public void findByIdTest() {
        Category genericEntity = Category.builder().name("categoryName").build();
        categoryService.save(genericEntity);

        Category foundEntity = categoryService.findById(genericEntity.getId());

        assertNotNull(foundEntity);
        assertEquals(genericEntity, foundEntity);
    }

    @Test
    public void saveTest() {
        Category genericEntity = Category.builder().name("categoryName").build();
        categoryService.save(genericEntity);

        Category foundEntity = categoryService.findById(1L);

        assertEquals(genericEntity, foundEntity);
    }

    @Test
    public void updateTest() {
        Category genericEntity = Category.builder().name("categoryName").build();
        categoryService.save(genericEntity);

        Category updated = Category.builder().id(1L).name("updatedName").build();
        categoryService.update(1L, updated);

        Category foundEntity = categoryService.findById(1L);

        assertEquals(updated, foundEntity);
    }

    @Test(expected = NotFoundException.class)
    public void deleteTest() {
        Category genericEntity = Category.builder().name("categoryName").build();
        categoryService.save(genericEntity);
        categoryService.delete(categoryService.findById(1L));

        categoryService.findById(1L);
    }

    @Test
    public void findAllTest() {
        List<Category> genericEntities = new ArrayList<>();
        List<Category> foundEntities;

        Category category1 = Category.builder().name("categoryName1").build();
        Category category2 = Category.builder().name("categoryName2").build();

        genericEntities.add(category1);
        genericEntities.add(category2);

        categoryService.save(category1);
        categoryService.save(category2);

        foundEntities = categoryService.findAll();

        assertNotNull(foundEntities);
        assertEquals(genericEntities, foundEntities);
    }
}

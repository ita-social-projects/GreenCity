package greencity.service.impl;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.exception.exceptions.BadCategoryRequestException;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.CategoryRepo;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class CategoryServiceImplTest {
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    public void saveTest() {
        Category genericEntity = new Category();
        when(categoryRepo.save(genericEntity)).thenReturn(genericEntity);
        assertEquals(genericEntity, categoryService.save(genericEntity));
    }

    @Test
    public void saveDtoTest() {
        CategoryDto genericDto = CategoryDto.builder().name("Test").build();
        Category genericEntity = Category.builder().name("Test").build();
        when(categoryService.save(genericDto)).thenReturn(genericEntity);
        assertEquals(genericEntity, categoryService.save(genericDto));
    }

    @Test
    public void saveDtoWhenFindByNameTrueTest() {
        when(categoryRepo.findByName(any())).thenReturn(new Category());
        assertThrows(BadCategoryRequestException.class, () -> {
            categoryService.save(new CategoryDto());
        });
    }

    @Test
    public void findByNameTest() {
        Category genericEntity = new Category();
        when(categoryRepo.findByName(any())).thenReturn(genericEntity);
        Category foundEntity = categoryService.findByName(any());
        assertEquals(genericEntity, foundEntity);
    }

    @Test
    public void findByNameWhenCategoryNullTest() {
        when(categoryRepo.findByName(anyString())).thenReturn(null);
        assertThrows(NotFoundException.class, () -> {
            categoryService.findByName(anyString());
        });
    }

    @Test
    public void findAllCategoryDtoTest() {
        List<Category> genericEntityList = Arrays.asList(
            Category.builder()
                .name("Test")
                .build(),
            Category.builder()
                .name("Test1")
                .build());
        when(categoryService.findAll()).thenReturn(genericEntityList);
        List<CategoryDto> mappedList = genericEntityList
            .stream()
            .map(category -> modelMapper.map(category, CategoryDto.class))
            .collect(Collectors.toList());
        List<CategoryDto> allCategoryDto = categoryService.findAllCategoryDto();
        assertEquals(mappedList, allCategoryDto);
    }

    @Test
    public void findByIdTest() {
        Category genericEntity = new Category();
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        Category foundEntity = categoryService.findById(anyLong());
        assertEquals(genericEntity, foundEntity);
    }

    @Test
    public void findByIdGivenIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> {
            categoryService.findById(null);
        });
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

    @Test
    public void updateGivenIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> {
            categoryService.update(null, new Category());
        });
    }

    @Test
    public void deleteByIdTest() {
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(new Category()));
        assertEquals(new Long(1), categoryService.deleteById(1L));
    }

    @Test
    public void deleteByIdGivenIdNullThenThrowException() {
        assertThrows(NotFoundException.class, () -> {
            categoryService.deleteById(null);
        });
    }

    @Test
    public void deleteByIdGivenCategoryRelatedToExistencePlaceThrowException() {
        Category generatedEntity = Category.builder().places(Collections.singletonList(new Place())).build();
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(generatedEntity));
        assertThrows(BadRequestException.class, () -> {
            categoryService.deleteById(1L);
        });
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

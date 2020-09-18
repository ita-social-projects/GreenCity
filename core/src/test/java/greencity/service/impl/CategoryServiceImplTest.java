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
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void saveTest() {
        Category genericEntity = new Category();
        when(categoryRepo.save(genericEntity)).thenReturn(genericEntity);
        assertEquals(genericEntity, categoryService.save(genericEntity));
    }

    @Test
    void saveDtoTest() {
        CategoryDto genericDto = CategoryDto.builder().name("Test").build();
        Category genericEntity = Category.builder().name("Test").build();
        when(categoryService.save(genericDto)).thenReturn(genericEntity);
        assertEquals(genericEntity, categoryService.save(genericDto));
    }

    @Test
    void saveDtoWhenFindByNameTrueTest() {
        CategoryDto dto = new CategoryDto();
        when(categoryRepo.findByName(any())).thenReturn(new Category());
        Assertions
            .assertThrows(BadCategoryRequestException.class,
                () -> categoryService.save(dto));
    }

    @Test
    void findByNameTest() {
        Category genericEntity = new Category();
        when(categoryRepo.findByName(any())).thenReturn(genericEntity);
        Category foundEntity = categoryService.findByName(any());
        assertEquals(genericEntity, foundEntity);
    }

    @Test
    void findByNameWhenCategoryNullTest() {
        when(categoryRepo.findByName(anyString())).thenReturn(null);
        Assertions
            .assertThrows(NotFoundException.class,
                () -> categoryService.findByName("test"));
    }

    @Test
    void findAllCategoryDtoTest() {
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
    void findByIdTest() {
        Category genericEntity = new Category();
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(genericEntity));
        Category foundEntity = categoryService.findById(anyLong());
        assertEquals(genericEntity, foundEntity);
    }

    @Test
    void findByIdGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> categoryService.findById(null));
    }

    @Test
    void updateTest() {
        Category updated = new Category();
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(updated));
        when(categoryRepo.save(any())).thenReturn(updated);
        categoryService.update(anyLong(), updated);
        Category foundEntity = categoryService.findById(anyLong());
        assertEquals(updated, foundEntity);
    }

    @Test
    void updateGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> categoryService.update(null, new Category()));
    }

    @Test
    void deleteByIdTest() {
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(new Category()));
        assertEquals(new Long(1), categoryService.deleteById(1L));
    }

    @Test
    void deleteByIdGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> categoryService.deleteById(null));
    }

    @Test
    void deleteByIdGivenCategoryRelatedToExistencePlaceThrowException() {
        Category generatedEntity = Category.builder().places(Collections.singletonList(new Place())).build();
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(generatedEntity));
        Assertions
            .assertThrows(BadRequestException.class,
                () -> categoryService.deleteById(1L));
    }

    @Test
    void findAllTest() {
        List<Category> genericEntities =
            new ArrayList<>(Arrays.asList(new Category(), new Category()));
        when(categoryRepo.findAll()).thenReturn(genericEntities);
        List<Category> foundEntities = categoryService.findAll();
        assertEquals(genericEntities, foundEntities);
    }
}

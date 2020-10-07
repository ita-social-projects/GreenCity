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

    private Category category = Category.builder()
        .name("Test")
        .build();

    private CategoryDto categoryDto = CategoryDto.builder()
        .name("Test")
        .build();

    @Test
    void saveTest() {
        when(categoryRepo.save(category)).thenReturn(category);
        assertEquals(category, categoryService.save(category));
    }

    @Test
    void saveDtoTest() {
        when(categoryService.save(categoryDto)).thenReturn(category);
        assertEquals(category, categoryService.save(categoryDto));
    }

    @Test
    void saveDtoWhenFindByNameTrueTest() {
        when(categoryRepo.findByName(any())).thenReturn(category);
        Assertions
            .assertThrows(BadCategoryRequestException.class,
                () -> categoryService.save(categoryDto));
    }

    @Test
    void findByNameTest() {
        when(categoryRepo.findByName(any())).thenReturn(category);
        Category foundEntity = categoryService.findByName(any());
        assertEquals(category, foundEntity);
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
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(category));
        Category foundEntity = categoryService.findById(anyLong());
        assertEquals(category, foundEntity);
    }

    @Test
    void findByIdGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> categoryService.findById(null));
    }

    @Test
    void updateTest() {
        when(categoryRepo.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepo.save(any())).thenReturn(category);
        categoryService.update(anyLong(), category);
        Category foundEntity = categoryService.findById(anyLong());
        assertEquals(category, foundEntity);
    }

    @Test
    void updateGivenIdNullThenThrowException() {
        Assertions
            .assertThrows(NotFoundException.class,
                () -> categoryService.update(null, category));
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

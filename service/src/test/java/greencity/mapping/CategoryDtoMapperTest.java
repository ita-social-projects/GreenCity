package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryDtoMapperTest {

    @InjectMocks
    CategoryDtoMapper categoryDtoMapper;

    @Test
    void convert() {
        CategoryDto categoryDto = CategoryDto.builder()
            .name("categoryDtoName")
            .build();
        Category expected = Category.builder()
            .name(categoryDto.getName())
            .build();
        assertEquals(expected, categoryDtoMapper.convert(categoryDto));
    }
}

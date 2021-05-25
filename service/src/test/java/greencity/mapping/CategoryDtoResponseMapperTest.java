package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;

import greencity.ModelUtils;
import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryDtoResponseMapperTest {

    @InjectMocks
    CategoryDtoResponseMapper categoryDtoResponseMapper;

    @Test
    void convert() {
        Category category = ModelUtils.getCategory();
        CategoryDtoResponse expected = CategoryDtoResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
        assertEquals(expected, categoryDtoResponseMapper.convert(category));
    }
}

package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.category.CategoryVO;
import greencity.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryVOMapperTest {
    @InjectMocks
    private CategoryVOMapper categoryVOMapper;

    @Test
    void convertTest() {
        Category category = ModelUtils.getCategory();

        CategoryVO expected = CategoryVO.builder()
            .id(category.getId())
            .name(category.getNameEn())
            .build();

        CategoryVO actual = categoryVOMapper.convert(category);

        assertEquals(expected, actual);
    }

    @Test
    void convertNullTest() {
        assertNull(categoryVOMapper.convert((Category) null));
    }

    @Test
    void convertEmptyTest() {
        Category category = new Category();

        CategoryVO result = categoryVOMapper.convert(category);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getName());
    }
}

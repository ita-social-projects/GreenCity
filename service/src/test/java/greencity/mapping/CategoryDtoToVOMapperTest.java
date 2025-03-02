package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CategoryDtoToVOMapperTest {
    @InjectMocks
    private CategoryDtoToVOMapper categoryDtoToVOMapper;

    @Test
    void convertTest() {
        CategoryDto testCategoryDto = CategoryDto.builder()
            .nameEn("Test name")
            .nameUk("Тестове ім'я")
            .parentCategoryId(1L)
            .build();

        CategoryVO expected = CategoryVO.builder()
            .name(testCategoryDto.getNameEn())
            .build();

        CategoryVO actual = categoryDtoToVOMapper.convert(testCategoryDto);

        assertEquals(expected, actual);
    }

    @Test
    void convertNullTest() {
        assertNull(categoryDtoToVOMapper.convert((CategoryDto) null));
    }

    @Test
    void convertEmptyTest() {
        CategoryDto testCategoryDto = new CategoryDto();

        CategoryVO result = categoryDtoToVOMapper.convert(testCategoryDto);

        assertNotNull(result);
        assertNull(result.getName());
    }
}

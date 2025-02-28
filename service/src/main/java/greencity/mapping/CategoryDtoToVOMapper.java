package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryVO;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoToVOMapper extends AbstractConverter<CategoryDto, CategoryVO> {
    @Override
    protected CategoryVO convert(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        return CategoryVO.builder()
                .name(categoryDto.getNameEn())
                .build();
    }
}

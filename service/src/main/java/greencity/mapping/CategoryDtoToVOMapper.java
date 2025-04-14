package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryVO;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link CategoryDto} into
 * {@link CategoryVO}.
 */
@Component
public class CategoryDtoToVOMapper extends AbstractConverter<CategoryDto, CategoryVO> {
    /**
     * Method for converting {@link CategoryDto} into {@link CategoryVO}.
     *
     * @param categoryDto object to convert.
     * @return converted object.
     */
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

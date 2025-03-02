package greencity.mapping;

import greencity.dto.category.CategoryVO;
import greencity.entity.Category;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Category} into
 * {@link CategoryVO}.
 */
@Component
public class CategoryVOMapper extends AbstractConverter<Category, CategoryVO> {
    /**
     * Method for converting {@link Category} into {@link CategoryVO}.
     *
     * @param category object to convert.
     * @return converted object.
     */
    @Override
    protected CategoryVO convert(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryVO.builder()
            .name(category.getNameEn())
            .id(category.getId())
            .build();
    }
}

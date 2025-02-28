package greencity.mapping;

import greencity.dto.category.CategoryVO;
import greencity.entity.Category;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CategoryVOMapper extends AbstractConverter<Category, CategoryVO> {

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

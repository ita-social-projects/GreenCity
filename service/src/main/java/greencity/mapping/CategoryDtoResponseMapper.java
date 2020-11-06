package greencity.mapping;

import greencity.dto.category.CategoryDtoResponse;
import greencity.entity.Category;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Category} into
 * {@link CategoryDtoResponse}.
 */
@Component
public class CategoryDtoResponseMapper extends AbstractConverter<Category, CategoryDtoResponse> {
    /**
     * Method convert {@link Category} to {@link CategoryDtoResponse}.
     *
     * @return {@link CategoryDtoResponse}
     */
    @Override
    protected CategoryDtoResponse convert(Category category) {
        return CategoryDtoResponse.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
    }
}

package greencity.mapping;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class CategoryDtoMapper extends AbstractConverter<CategoryDto, Category> {
    @Override
    protected Category convert(CategoryDto categoryDto) {
        return Category.builder()
            .name(categoryDto.getName())
            .build();
    }
}

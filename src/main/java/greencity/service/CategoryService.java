package greencity.service;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;

import java.util.List;

public interface CategoryService {
    Category save(CategoryDto dto);

    Category save(Category category);

    Category update(Category category);

    Category findById(Long id);

    List<Category> findAll();

    void deleteById(Long id);

    Boolean existsByName(String name);

}

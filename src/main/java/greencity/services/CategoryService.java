package greencity.services;

import greencity.entities.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Long id);

    Category save(Category category);

    Category update(Long id, Category category);

    void delete(Category category);
}
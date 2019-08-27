package greencity.service;

import greencity.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Long id);

    Category update(Long id, Category category);

    void deleteById(Long id);
}

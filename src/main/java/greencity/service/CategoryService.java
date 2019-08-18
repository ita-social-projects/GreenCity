package greencity.service;


import greencity.entity.Category;

import java.util.List;

public interface CategoryService {
    Category save(Category category);

    Category update(Category category);

    Category findById(Long id);

    List<Category> findAll();

    void deleteById(Long id);

}

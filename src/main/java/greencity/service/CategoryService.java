package greencity.service;

import greencity.dto.category.CategoryDto;
import greencity.entity.Category;

import java.util.List;

/** Provides the interface to manage {@code Category} entity. */
public interface CategoryService {

    /**
     * Method for saving new Category to database.
     *
     * @param dto - dto for Category entity.
     * @return a category.
     */
    Category save(CategoryDto dto);

    /**
     * Save Category to DB.
     *
     * @param category - entity of Category.
     * @return saved Category.
     */
    Category save(Category category);

    /**
     * Method for updating Category.
     *
     * @param id - category id.
     * @param category - Category entity.
     * @return a category.
     */
    Category update(Long id, Category category);

    /**
     * Method for finding Category by id.
     *
     * @param id - category's id.
     * @return a category.
     */
    Category findById(Long id);

    /**
     * Method for finding all Categories.
     *
     * @return list of Categories.
     */
    List<Category> findAll();

    /**
     * Method for deleting Category by id.
     *
     * @param id - category's id.
     */
    void deleteById(Long id);

    /**
     * Finds category by name.
     *
     * @param name to find by.
     * @return a category by name.
     */
    Category findByName(String name);

    List<CategoryDto> findAllCategoryDto();
}

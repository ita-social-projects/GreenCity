package greencity.service;

import greencity.dto.category.CategoryDto;
import greencity.dto.category.CategoryDtoResponse;
import java.util.List;

/**
 * Provides the interface to manage {@code Category} entity.
 */
public interface CategoryService {
    /**
     * Method for saving new Category to database.
     *
     * @param dto - dto for Category entity.
     * @return a category.
     */
    CategoryDtoResponse save(CategoryDto dto);

    /**
     * Method for updating Category.
     *
     * @param id   - category id.
     * @param name - Category name.
     * @return a category.
     */
    CategoryDtoResponse update(Long id, String name);

    /**
     * Method for finding Category by id.
     *
     * @param id - category's id.
     * @return a category.
     */
    CategoryDtoResponse findById(Long id);

    /**
     * Method for finding all Categories.
     *
     * @return list of Categories.
     */
    List<CategoryDtoResponse> findAll();

    /**
     * Method for deleting Category by id.
     *
     * @param id - category's id.
     * @return id of deleted Category.
     */
    Long deleteById(Long id);

    /**
     * Finds category by name.
     *
     * @param name to find by.
     * @return a category by name.
     */
    CategoryDtoResponse findByName(String name);

    /**
     * Method for finding all CategoryDto.
     *
     * @return list of CategoryDto.
     */
    List<CategoryDto> findAllCategoryDto();
}

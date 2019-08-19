package greencity.services.impl;

import greencity.entities.Category;
import greencity.exceptions.NotFoundException;
import greencity.repositories.CategoryRepo;
import greencity.services.CategoryService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Category entity.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    /**
     * Find all categories from DB.
     *
     * @return List of categories.
     * @author Nazar Vladyka
     */
    @Override
    public List<Category> findAll() {
        log.info("in findAll()");

        return categoryRepo.findAll();
    }

    /**
     * Find Category entity by id.
     *
     * @param id - Category id.
     * @return Category entity.
     * @author Nazar Vladyka
     */
    @Override
    public Category findById(Long id) {
        log.info("in findById(Long id), id - {}", id);

        return categoryRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with id " + id));
    }

    /**
     * Save Category to DB.
     *
     * @param category - entity of Category.
     * @return saved Category.
     * @author Nazar Vladyka
     */
    @Override
    public Category save(Category category) {
        log.info("in save(Category category), {}", category);

        return categoryRepo.saveAndFlush(category);
    }

    /**
     * Update Category in DB.
     *
     * @param id - Category id.
     * @param category - Category entity.
     * @return Category updated entity.
     * @author Nazar Vladyka
     */
    @Override
    public Category update(Long id, Category category) {
        Category updatable = findById(id);

        updatable.setName(category.getName());
        updatable.setParentCategory(category.getParentCategory());
        updatable.setCategories(category.getCategories());
        updatable.setPlaces(category.getPlaces());

        log.info("in update(Long id, Category category), {}", category);

        return categoryRepo.saveAndFlush(updatable);
    }

    /**
     * Delete entity from DB.
     *
     * @param category - Category entity.
     * @author Nazar Vladyka
     */
    @Override
    public void delete(Category category) {
        log.info("in delete(Category category), {}", category);

        categoryRepo.delete(category);
    }
}

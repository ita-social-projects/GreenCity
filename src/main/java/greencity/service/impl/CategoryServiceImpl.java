package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.entity.Category;
import greencity.exception.NotFoundException;
import greencity.repository.CategoryRepo;
import greencity.service.CategoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        log.info(LogMessage.IN_FIND_ALL);

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
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return categoryRepo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND_BY_ID + id));
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
        log.info(LogMessage.IN_UPDATE, category);

        Category updatable =
                categoryRepo
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                ErrorMessage.CATEGORY_NOT_FOUND_BY_ID + id));

        updatable.setName(category.getName());
        updatable.setParentCategory(category.getParentCategory());
        updatable.setCategories(category.getCategories());
        updatable.setPlaces(category.getPlaces());

        return categoryRepo.save(category);
    }

    /**
     * Delete entity from DB by id.
     *
     * @param id - Category id.
     * @author Nazar Vladyka
     */
    @Override
    public void deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        categoryRepo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND_BY_ID + id));

        categoryRepo.deleteById(id);
    }
}

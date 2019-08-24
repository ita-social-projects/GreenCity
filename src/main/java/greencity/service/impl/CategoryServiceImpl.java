package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import greencity.exception.BadCategoryRequestException;
import greencity.exception.BadIdException;
import greencity.repository.CategoryRepo;
import greencity.service.CategoryService;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** The class provides implementation of the {@code CategoryService}. */
@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepo categoryRepo;

    /**
     * Method for saving Category to database.
     *
     * @param dto - dto for Category entity
     * @return category
     * @author Kateryna Horokh
     */
    @Override
    public Category save(CategoryDto dto) {
        log.info("In save category method");

        Category byName = categoryRepo.findByName(dto.getName());

        if (byName != null) {
            throw new BadCategoryRequestException(
                    ErrorMessage.CATEGORY_ALREADY_EXISTS_BY_THIS_NAME);
        }
        return categoryRepo.save(Category.builder().name(dto.getName()).build());
    }

    @Override
    public Category update(Category category) {
        return null;
    }

    @Override
    public Category save(Category category) {
        return categoryRepo.save(category);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepo
                .findById(id)
                .orElseThrow(() -> new BadIdException(ErrorMessage.CATEGORY_NOT_FOUND_BY_ID + id));
    }

    @Override
    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        Category category = findById(id);
        categoryRepo.delete(category);
    }

    @Override
    public Category findByName(String name) {
        return categoryRepo.findByName(name);
    }
}

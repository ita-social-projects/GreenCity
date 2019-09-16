package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.category.CategoryDto;
import greencity.entity.Category;
import greencity.exception.BadCategoryRequestException;
import greencity.exception.BadRequestException;
import greencity.exception.NotFoundException;
import greencity.repository.CategoryRepo;
import greencity.service.CategoryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Category entity.
 *
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;

    private ModelMapper modelMapper;

    /**
     * Method for saving Category to database.
     *
     * @param dto - dto for Category entity
     * @return category
     * @author Kateryna Horokh
     */
    @Override
    public Category save(CategoryDto dto) {
        log.info(LogMessage.IN_SAVE);

        Category byName = categoryRepo.findByName(dto.getName());

        if (byName != null) {
            throw new BadCategoryRequestException(
                ErrorMessage.CATEGORY_ALREADY_EXISTS_BY_THIS_NAME);
        }
        return categoryRepo.save(Category.builder().name(dto.getName()).build());
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public Category save(Category category) {
        log.info(LogMessage.IN_SAVE, category);

        return categoryRepo.save(category);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public List<Category> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return categoryRepo.findAll();
    }

    /**
     * {@inheritDoc}
     *
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
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public Category update(Long id, Category category) {
        log.info(LogMessage.IN_UPDATE, category);

        Category updatable = findById(id);

        updatable.setName(category.getName());
        updatable.setParentCategory(category.getParentCategory());
        updatable.setCategories(category.getCategories());
        updatable.setPlaces(category.getPlaces());

        return categoryRepo.save(category);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public Long deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);
        Category category = findById(id);

        if (!category.getPlaces().isEmpty()) {
            throw new BadRequestException(ErrorMessage.NOT_SAVE_DELETION);
        }

        categoryRepo.delete(category);
        return id;
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public Category findByName(String name) {
        Category category = categoryRepo.findByName(name);
        if (category == null) {
            throw new NotFoundException(ErrorMessage.CATEGORY_NOT_FOUND_BY_NAME + name);
        }
        return categoryRepo.findByName(name);
    }

    /**
     * {@inheritDoc}
     *
     * @author Kateryna Horokh
     */
    @Override
    public List<CategoryDto> findAllCategoryDto() {
        List<Category> categories = findAll();
        return categories.stream()
            .map(category -> modelMapper.map(category, CategoryDto.class))
            .collect(Collectors.toList());
    }
}

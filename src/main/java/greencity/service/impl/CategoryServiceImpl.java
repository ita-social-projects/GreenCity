package greencity.service.impl;

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

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepo categoryRepo;

    @Override
    public Category save(CategoryDto dto) {
        log.info("In save method");
        Boolean byName = categoryRepo.existsByName(dto.getName());
        if (byName) {
            throw new BadCategoryRequestException("Category by this name already exist.");
        }
        log.info("Ready for creating new Category");
        return categoryRepo.save(Category.builder().name(dto.getName()).build());
    }

    @Override
    public Category update(Category category) {
        return null;
    }

    @Override
    public Category findById(Long id) {
        return categoryRepo
                .findById(id)
                .orElseThrow(() -> new BadIdException("No category with this id:" + id));
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
    public Boolean existsByName(String name) {
        return categoryRepo.existsByName(name);
    }
}

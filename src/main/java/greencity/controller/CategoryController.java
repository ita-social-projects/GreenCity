package greencity.controller;

import greencity.dto.category.CategoryDto;
import greencity.service.CategoryService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    /**
     * The method which return new {@code Category}.
     *
     * @param dto - CategoryDto dto for adding with all parameters.
     * @return new {@code Category}.
     * @author Kateryna Horokh
     */
    @PostMapping("/save")
    public ResponseEntity saveCategory(@Valid @RequestBody CategoryDto dto) {
        return  ResponseEntity.status(HttpStatus.CREATED).body( categoryService.save(dto));
    }

    /**
     * The method which return all {@code Category}.

     * @return list of {@code Category}.
     * @author Kateryna Horokh
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> findAllCategory() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAllCategoryDto());
    }
}

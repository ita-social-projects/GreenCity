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

    @PostMapping("/save")
    public ResponseEntity saveCategory(@Valid @RequestBody CategoryDto dto) {
        return  ResponseEntity.status(HttpStatus.CREATED).body( categoryService.save(dto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> findAllCategory() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAllCategoryDto());
    }
}

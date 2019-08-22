package greencity.controller;

import greencity.dto.category.CategoryDto;
import greencity.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {

    private CategoryService categoryService;

    @PostMapping("/save")
    public ResponseEntity<?> saveCategory(@Valid @RequestBody CategoryDto dto) {
        categoryService.save(dto);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}

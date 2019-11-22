package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.category.CategoryDto;
import greencity.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
@AllArgsConstructor
public class CategoryController {
    private CategoryService categoryService;

    /**
     * The method which returns new {@code Category}.
     *
     * @param dto - CategoryDto dto for adding with all parameters.
     * @return new {@code Category}.
     * @author Kateryna Horokh
     */
    @ApiOperation(value = "Save category")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping
    public ResponseEntity saveCategory(@Valid @RequestBody CategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(dto));
    }

    /**
     * The method which returns all {@code Category}.
     *
     * @return list of {@code Category}.
     * @author Kateryna Horokh
     */
    @ApiOperation(value = "View a list of available categories")
    @ApiResponses(value = {
        @ApiResponse( code = 200, message = "Successfully retrieved list"),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER)
    })
    @GetMapping
    public ResponseEntity<List<CategoryDto>> findAllCategory() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findAllCategoryDto());
    }
}

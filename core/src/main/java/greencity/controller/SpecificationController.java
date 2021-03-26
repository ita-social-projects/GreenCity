package greencity.controller;

import greencity.dto.specification.SpecificationNameDto;
import greencity.service.SpecificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/specification")
@AllArgsConstructor
public class SpecificationController {
    private SpecificationService specificationService;

    /**
     * The method which returns all {@code SpecificationNameDto}.
     *
     * @return list of {@code SpecificationNameDto}.
     * @author Kateryna Horokh
     */
    @GetMapping
    @ApiOperation(value = "Find all specifications")
    @ApiResponses({
        @ApiResponse(code = 201, message = "All specifications")
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<List<SpecificationNameDto>> findAllSpecification() {
        return ResponseEntity.status(HttpStatus.CREATED).body(specificationService.findAllSpecificationDto());
    }
}

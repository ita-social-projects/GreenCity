package greencity.webcontroller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.FieldErrorDto;
import greencity.dto.genericresponse.GenericResponseDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.specification.SpecificationNameDto;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.service.CategoryService;
import greencity.service.PlaceService;
import greencity.service.SpecificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/places")
public class ManagementPlacesController {
    private final PlaceService placeService;
    private final CategoryService categoryService;
    private final SpecificationService specificationService;

    /**
     * Returns management page with all places.
     *
     * @param query    Query for searching related data
     * @param model    ModelAndView that will be configured.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Olena Petryshak
     */
    @GetMapping("")
    public String getAllPlaces(@RequestParam(required = false, name = "query") String query, Model model,
                               @ApiIgnore Pageable pageable) {
        PageableDto<AdminPlaceDto> allPlaces =
            query == null || query.isEmpty() ? placeService.findAll(pageable) : placeService.searchBy(pageable, query);
        model.addAttribute("pageable", allPlaces);
        model.addAttribute("categoryList", categoryService.findAllCategoryDto());
        List<String> discountSpecifications = specificationService.findAllSpecificationDto().stream().map(
            SpecificationNameDto::getName).collect(Collectors.toList());
        model.addAttribute("discountSpecifications", discountSpecifications);
        return "core/management_places";
    }

    /**
     * Method which saves {@link Place}.
     *
     * @param placeAddDto dto with info for registering place.
     * @param user        is an admin
     * @return {@link GenericResponseDto}
     */
    @PostMapping("/")
    @ResponseBody
    public GenericResponseDto savePlace(@Valid @RequestBody PlaceAddDto placeAddDto, BindingResult bindingResult,
                                        @ApiIgnore @CurrentUser User user) {
        if (bindingResult.hasErrors()) {
            GenericResponseDto genericResponseDto = new GenericResponseDto();
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                genericResponseDto.getErrors().add(
                    new FieldErrorDto(fieldError.getField(), fieldError.getDefaultMessage()));
            }
            return genericResponseDto;
        }
        placeService.save(placeAddDto, user.getEmail());
        return GenericResponseDto.builder().build();
    }

    /**
     * Method for deleting {@link Place} by given id.
     *
     * @param id {@link Place} id.
     * @return {@link ResponseEntity}.
     */
    @DeleteMapping
    public ResponseEntity<Long> delete(@RequestParam("id") Long id) {
        placeService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    @ApiOperation(value = "Get all Places by given IDs")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })

    /**
     * Method for deleting {@link Place} by given list of ids.
     *
     * @param listId list of IDs.
     * @return {@link ResponseEntity}.
     */
    @DeleteMapping("/deleteAll")
    public ResponseEntity<List<Long>> deleteAll(@RequestBody List<Long> listId) {
        placeService.bulkDelete(listId);
        return ResponseEntity.status(HttpStatus.OK)
            .body(listId);
    }
}


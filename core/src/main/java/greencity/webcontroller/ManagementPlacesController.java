package greencity.webcontroller;

import greencity.annotations.CurrentUser;
import greencity.dto.PageableDto;
import greencity.dto.genericresponse.GenericResponseDto;
import static greencity.dto.genericresponse.GenericResponseDto.buildGenericResponseDto;

import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceUpdateDto;
import greencity.dto.specification.SpecificationNameDto;
import greencity.dto.user.UserVO;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.service.CategoryService;
import greencity.service.PlaceService;
import greencity.service.SpecificationService;
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
     * Method for getting PlaceUpdateDto by id.
     *
     * @return {@link PlaceUpdateDto} instance.
     */
    @GetMapping("/find")
    public ResponseEntity<PlaceUpdateDto> getPlaceById(@RequestParam("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(placeService.getInfoForUpdatingById(id));
    }

    /**
     * Method which saves {@link Place}.
     *
     * @param placeAddDto dto with info for registering place.
     * @param user  {@link User} is an admin
     * @return {@link GenericResponseDto}
     */
    @PostMapping
    @ResponseBody
    public GenericResponseDto savePlace(@Valid @RequestBody PlaceAddDto placeAddDto,
                                        BindingResult bindingResult,
                                        @ApiIgnore @CurrentUser User user) {
        if (!bindingResult.hasErrors()) {
            placeService.save(placeAddDto, user.getEmail());
        }
        return buildGenericResponseDto(bindingResult);
    }

    /**
     * Method which updates {@link Place}.
     *
     * @param placeUpdateDto of {@link PlaceUpdateDto}
     * @return {@link GenericResponseDto}
     */
    @ResponseBody
    @PutMapping
    public GenericResponseDto updatePlace(@Valid @RequestBody PlaceUpdateDto placeUpdateDto,
                                          BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            placeService.update(placeUpdateDto);
        }
        return buildGenericResponseDto(bindingResult);
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


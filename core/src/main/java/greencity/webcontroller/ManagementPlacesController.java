package greencity.webcontroller;

import greencity.dto.PageableDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@AllArgsConstructor
@RequestMapping("/management/places")
public class ManagementPlacesController {
    private final PlaceService placeService;

    /**
     * Returns management page with all places.
     *
     * @param query    Query for searching related data
     * @param model    ModelAndView that will be configured.
     * @param pageable {@link Pageable}.
     * @return View template path {@link String}.
     * @author Olena Petryshak
     */
    @GetMapping
    public String getAllPlaces(@RequestParam(required = false, name = "query") String query, Model model,
                               @ApiIgnore Pageable pageable) {
        PageableDto<AdminPlaceDto> allPlaces =
            query == null || query.isEmpty() ? placeService.findAll(pageable) : placeService.searchBy(pageable, query);
        model.addAttribute("pageable", allPlaces);
        return "core/management_places";
    }
}

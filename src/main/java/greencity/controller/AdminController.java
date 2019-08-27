package greencity.controller;

import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceStatusDto;
import greencity.entity.enums.PlaceStatus;
import greencity.service.PlaceService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    /** Autowired PlaceService instance. */
    private final PlaceService placeService;

    /**
     * The method parse the string param to PlaceStatus value.
     *
     * @param status a string represents {@link PlaceStatus} enum value.
     * @return response object with list of dto. The list can be empty.
     * @author Roman Zahorui
     */
    @GetMapping("/places")
    public ResponseEntity<List<AdminPlaceDto>> getPlacesByStatus(@RequestParam String status) {
        PlaceStatus placeStatus = PlaceStatus.valueOf(status.toUpperCase());
        return new ResponseEntity<>(placeService.getPlacesByStatus(placeStatus), HttpStatus.OK);
    }

    /**
     * The method which change place status.
     *
     * @param placeStatusDto a dto for accepting changes in place status.
     * @return response object this OK status if everything is ok.
     * @author Nazar Vladyka
     */
    @PostMapping("places/proposed/changeStatus")
    public ResponseEntity changePlaceStatus(@Validated @RequestBody PlaceStatusDto placeStatusDto) {
        placeService.updateStatus(placeStatusDto.getId(), placeStatusDto.getStatus());
        return new ResponseEntity(HttpStatus.OK);
    }
}

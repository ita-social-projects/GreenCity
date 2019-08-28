package greencity.controller;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.service.PlaceService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/place")
@AllArgsConstructor
public class PlaceController {

    /** Autowired PlaceService instance. */
    private PlaceService placeService;

    @PostMapping("/propose")
    public ResponseEntity<?> proposePlace(@Valid @RequestBody PlaceAddDto dto) {
        placeService.save(dto);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @GetMapping("/places")
    public ResponseEntity<List<Place>> findAllCategory() {
        return ResponseEntity.status(HttpStatus.OK).body(placeService.findAll());
    }

    /**
     * Controller to get place info
     *
     * @param id place
     * @return info about place
     */
    @GetMapping("/getInfo/{id}")
    public ResponseEntity<?> getInfo(@NotNull @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(placeService.getAccessById(id));
    }

    @PostMapping("/getListPlaceLocationByMapsBounds")
    public ResponseEntity<List<PlaceByBoundsDto>> getListPlaceLocationByMapsBounds(
            @Valid @RequestBody MapBoundsDto mapBoundsDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeService.findPlacesByMapsBounds(mapBoundsDto));
    }

    /**
     * The method parse the string param to PlaceStatus value.
     *
     * @param status a string represents {@link PlaceStatus} enum value.
     * @return response object with list of dto. The list can be empty.
     * @author Roman Zahorui
     */
    @GetMapping("/admin/places")
    public ResponseEntity<List<AdminPlaceDto>> getPlacesByStatus(@RequestParam String status) {
        PlaceStatus placeStatus = PlaceStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok().body(placeService.getPlacesByStatus(placeStatus));
    }
}

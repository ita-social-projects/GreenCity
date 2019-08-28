package greencity.controller;

import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.service.PlaceService;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

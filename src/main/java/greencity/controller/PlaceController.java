package greencity.controller;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceStatusDto;
import greencity.entity.Place;
import greencity.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    /**
     * The method which return a list {@code PlaceByBoundsDto} with information about place,
     * location depends on the map bounds.
     *
     * @param mapBoundsDto Contains South-West and North-East bounds of map .
     * @return a list of {@code PlaceByBoundsDto}
     * @author Marian Milian
     */
    @PostMapping("/getListPlaceLocationByMapsBounds")
    public ResponseEntity<List<PlaceByBoundsDto>> getListPlaceLocationByMapsBounds(
            @Valid @RequestBody MapBoundsDto mapBoundsDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeService.findPlacesByMapsBounds(mapBoundsDto));
    }

    /**
     * The method which change place status.
     *
     * @param dto - place dto with place id and updated place status.
     * @return response object with dto and OK status if everything is ok.
     * @author Nazar Vladyka
     */
    @PatchMapping("/changeStatus")
    public ResponseEntity changePlaceStatus(@Valid @RequestBody PlaceStatusDto dto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeService.updateStatus(dto.getId(), dto.getStatus()));
    }
}

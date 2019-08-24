package greencity.controller;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.service.PlaceService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/place")
@AllArgsConstructor
public class PlaceController {

    private PlaceService placeService;

    @PostMapping("/propose")
    public void proposePlace(@Valid @RequestBody PlaceAddDto dto) {
        placeService.save(dto);
    }

    @PostMapping("/getListPlaceLocationByMapsBounds")
    public ResponseEntity<List<PlaceByBoundsDto>> getListPlaceLocationByMapsBounds(
            @Valid @RequestBody MapBoundsDto mapBoundsDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeService.findPlacesByMapsBounds(mapBoundsDto));
    }
}

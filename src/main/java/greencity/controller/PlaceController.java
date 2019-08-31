package greencity.controller;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.*;
import greencity.entity.enums.PlaceStatus;
import greencity.service.PlaceService;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private ModelMapper modelMapper;

    /**
     * The method which return new proposed {@code Place} from user.
     *
     * @param dto - Place dto for adding with all parameters.
     * @return new {@code Place}.
     * @author Kateryna Horokh
     */
    @PostMapping("/propose")
    public ResponseEntity<PlaceWithUserDto> proposePlace(
            @Valid @RequestBody PlaceAddDto dto, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                modelMapper.map(
                        placeService.save(dto, principal.getName()), PlaceWithUserDto.class));
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
     * The method parse the string param to PlaceStatus value.
     *
     * @param status a string represents {@link PlaceStatus} enum value.
     * @return response object with list of dto. The list can be empty.
     * @author Roman Zahorui
     */
    @GetMapping("/{status}")
    public ResponseEntity<List<AdminPlaceDto>> getPlacesByStatus(@PathVariable String status) {
        PlaceStatus placeStatus = PlaceStatus.valueOf(status.toUpperCase());
        return ResponseEntity.status(HttpStatus.OK)
                .body(placeService.getPlacesByStatus(placeStatus));
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

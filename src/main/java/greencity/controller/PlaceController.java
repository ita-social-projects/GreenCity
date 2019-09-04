package greencity.controller;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.*;
import greencity.entity.enums.PlaceStatus;
import greencity.service.FavoritePlaceService;
import greencity.service.PlaceService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/place")
@AllArgsConstructor
public class PlaceController {
    /**
     * Autowired PlaceService instance.
     */
    private PlaceService placeService;

    private final FavoritePlaceService favoritePlaceService;

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
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                modelMapper.map(
                    placeService.save(dto, principal.getName()),
                    PlaceWithUserDto.class));
    }

    /**
     * Controller to get place info.
     *
     * @param id place
     * @return info about place
     */
    @GetMapping("/Info/{id}")
    public ResponseEntity<?> getInfo(@NotNull @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(placeService.getInfoById(id));
    }

    /**
     * Controller to get favorite place as place info.
     *
     * @param id favorite place
     * @return info about place with name as in favorite place
     */
    @GetMapping("/info/favorite")
    public ResponseEntity<PlaceInfoDto> getFavoritePlaceInfo(@NotNull @RequestParam Long id) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(favoritePlaceService.getInfoFavoritePlace(id));
    }

    /**
     * Generated javadoc, must be replaced with real one.
     */
    @PostMapping("/save/favorite")
    public ResponseEntity<FavoritePlaceDto> saveAsFavoritePlace(
        @Valid @RequestBody FavoritePlaceDto favoritePlaceDto, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(favoritePlaceService.save(favoritePlaceDto, principal.getName()));
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
     * @param status   a string represents {@link PlaceStatus} enum value.
     * @param pageable pageable configuration.
     * @return response {@link PlacePageableDto} object. Contains a list of {@link AdminPlaceDto}.
     * @author Roman Zahorui
     */
    @GetMapping("/{status}")
    public ResponseEntity<PlacePageableDto> getPlacesByStatus(
        @PathVariable String status, Pageable pageable) {
        PlaceStatus placeStatus = PlaceStatus.valueOf(status.toUpperCase());
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeService.getPlacesByStatus(placeStatus, pageable));
    }

    /**
     * The method which update place status.
     *
     * @param dto - place dto with place id and updated place status.
     * @return response object with dto and OK status if everything is ok.
     * @author Nazar Vladyka
     */
    @PatchMapping("/status")
    public ResponseEntity updateStatus(@Valid @RequestBody PlaceStatusDto dto) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(placeService.updateStatus(dto.getId(), dto.getStatus()));
    }
}

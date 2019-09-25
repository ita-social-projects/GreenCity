package greencity.controller;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.service.FavoritePlaceService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite_place/")
@AllArgsConstructor
public class FavoritePlaceController {
    private final FavoritePlaceService favoritePlaceService;

    /**
     * Update favorite place name for user.
     *
     * @param favoritePlaceDto - dto for FavoritePlace entity
     * @param principal        - Principal with user email
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    @PutMapping
    public ResponseEntity<FavoritePlaceDto> update(@Valid @RequestBody FavoritePlaceDto favoritePlaceDto,
                                                   Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .update(favoritePlaceDto, principal.getName()));
    }

    /**
     * Find all favorite places by user email.
     *
     * @param principal - Principal with user email
     * @return list of dto
     * @author Zakhar Skaletskyi
     */

    @GetMapping
    public ResponseEntity<List<FavoritePlaceDto>> findAllByUserEmail(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService.findAllByUserEmail(principal.getName()));
    }


    /**
     * Delete favorite place by user email and place id or favorite place id.
     * If id>0 then delete by favorite place id. If id<0 then delete by place id.
     *
     * @param placeId   - place id
     * @param principal - Principal with user email
     * @return id of deleted favorite place
     * @author Zakhar Skaletskyi
     */
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Long> deleteByUserEmailAndPlaceId(@PathVariable Long placeId,
                                                                     Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .deleteByUserEmailAndPlaceId(placeId, principal.getName()));
    }

    /**
     * Controller to get favorite place coordinates, id and name.
     *
     * @param placeId   favorite place
     * @param principal - Principal with user email
     * @return info about place with name from favorite place
     * @author Zakhar Skaletskyi
     */
    @GetMapping("/favorite/{placeId}")
    public ResponseEntity<PlaceByBoundsDto> getFavoritePlaceWithCoordinate(@PathVariable Long placeId,
                                                                           Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .getFavoritePlaceWithLocation(placeId, principal.getName()));
    }
}
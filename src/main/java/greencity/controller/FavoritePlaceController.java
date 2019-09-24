package greencity.controller;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.favoriteplace.FavoritePlaceShowDto;
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
     * @param favoritePlaceShowDto - dto for FavoritePlace entity
     * @param principal            - Principal with user email
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    @PutMapping
    public ResponseEntity<FavoritePlaceShowDto> update(@Valid @RequestBody FavoritePlaceShowDto favoritePlaceShowDto,
                                                       Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .update(favoritePlaceShowDto, principal.getName()));
    }

    /**
     * Find all favorite places by user email.
     *
     * @param principal - Principal with user email
     * @return list of dto
     * @author Zakhar Skaletskyi
     */

    @GetMapping
    public ResponseEntity<List<FavoritePlaceShowDto>> findAllByUserEmail(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService.findAllByUserEmail(principal.getName()));
    }

    /**
     * Find all favorite places names with placeId by user email.
     *
     * @param principal - Principal with user email
     * @return list of dto
     * @author Zakhar Skaletskyi
     */

    @GetMapping("/with_place_id")
    public ResponseEntity<List<FavoritePlaceDto>> findAllByUserEmailWithPlaceId(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .getFavoritePlaceWithPlaceId(principal.getName()));
    }

    /**
     * Delete favorite place by user email and place id or favorite place id.
     * If id>0 then delete by favorite place id. If id<0 then delete by place id.
     *
     * @param id        - favorite place
     * @param principal - Principal with user email
     * @return id of deleted favorite place
     * @author Zakhar Skaletskyi
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteByUserEmailAndFavoriteIdOrPlaceId(@NotNull @PathVariable Long id,
                                                       Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .deleteByUserEmailAndFavoriteIdOrPlaceId(id, principal.getName()));
    }

    /**
     * Controller to get favorite place coordinates, id and name.
     *
     * @param id        favorite place
     * @param principal - Principal with user email
     * @return info about place with name from favorite place
     * @author Zakhar Skaletskyi
     */
    @GetMapping("/favorite/{id}")
    public ResponseEntity<PlaceByBoundsDto> getFavoritePlaceWithCoordinate(@NotNull @PathVariable Long id,
                                                                           Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .getFavoritePlaceWithLocation(id, principal.getName()));
    }
}
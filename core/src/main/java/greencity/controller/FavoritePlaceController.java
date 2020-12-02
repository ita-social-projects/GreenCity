package greencity.controller;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.FavoritePlace;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.service.FavoritePlaceService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/favorite_place/")
@AllArgsConstructor
public class FavoritePlaceController {
    private final FavoritePlaceService favoritePlaceService;

    /**
     * Update {@link FavoritePlace} name for {@link User}.
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param favoritePlaceDto - dto for {@link FavoritePlace} entity
     * @param principal        - Principal with user email
     * @return {@link FavoritePlaceDto} instance
     * @author Zakhar Skaletskyi
     */
    @PutMapping
    public ResponseEntity<FavoritePlaceDto> update(@Valid @RequestBody FavoritePlaceDto favoritePlaceDto,
                                                   @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .update(favoritePlaceDto, principal.getName()));
    }

    /**
     * Find all {@link FavoritePlace} by {@link User} email.
     * Parameter principal are ignored because Spring automatically provide the Principal object
     * .
     * @param principal - Principal with {@link User} email
     * @return list of {@link FavoritePlaceDto}
     * @author Zakhar Skaletskyi
     */

    @GetMapping
    public ResponseEntity<List<FavoritePlaceDto>> findAllByUserEmail(@ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService.findAllByUserEmail(principal.getName()));
    }


    /**
     * Delete {@link FavoritePlace} by {@link User} email and {@link Place} id
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param placeId   - {@link Place} id
     * @param principal - Principal with {@link User} email
     * @return id of deleted {@link FavoritePlace}
     * @author Zakhar Skaletskyi
     */
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Long> deleteByUserEmailAndPlaceId(@PathVariable Long placeId,
                                                            @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .deleteByUserEmailAndPlaceId(placeId, principal.getName()));
    }

    /**
     * Controller to get {@link FavoritePlace} coordinates, id and name.
     * Parameter principal are ignored because Spring automatically provide the Principal object.
     *
     * @param placeId   - {@link Place} id
     * @param principal - Principal with {@link User} email
     * @return info about {@link Place} with name from {@link FavoritePlace}
     * @author Zakhar Skaletskyi
     */
    @GetMapping("/favorite/{placeId}")
    public ResponseEntity<PlaceByBoundsDto> getFavoritePlaceWithCoordinate(@PathVariable Long placeId, @ApiIgnore
                                                                           Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .getFavoritePlaceWithLocation(placeId, principal.getName()));
    }
}
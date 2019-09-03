package greencity.controller;

import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.service.FavoritePlaceService;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite_place")
@AllArgsConstructor
public class FavoritePlaceController {
    private final FavoritePlaceService favoritePlaceService;

    /**
     * Update favorite place name for user.
     *
     * @param favoritePlaceDto - dto for FavoritePlace entity
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
     * Delete favorite place by place id and user email.
     *
     * @param placeId   - place id
     * @param principal - Principal with user email
     * @author Zakhar Skaletskyi
     */
    @SuppressWarnings("checkstyle:GenericWhitespace")
    @DeleteMapping
    public ResponseEntity<Integer> deleteByPlaceIdAndUserEmail(@Valid @RequestParam Long placeId,
                                                               Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .deleteByPlaceIdAndUserEmail(placeId, principal.getName()));
    }
}



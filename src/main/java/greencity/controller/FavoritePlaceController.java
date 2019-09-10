package greencity.controller;

import greencity.dto.favoriteplace.FavoritePlaceShowDto;
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
     * Delete favorite place by id and user email.
     *
     * @param id        - favorite place
     * @param principal - Principal with user email
     * @return id of deleted favorite place
     * @author Zakhar Skaletskyi
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteByIdAndUserEmail(@NotNull  @PathVariable Long id,
                                                       Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .deleteByIdAndUserEmail(id, principal.getName()));
    }
}
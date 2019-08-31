package greencity.controller;

import greencity.dto.favoritePlace.FavoritePlaceDto;
import greencity.service.FavoritePlaceService;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/favorite_place")
@AllArgsConstructor
public class FavoritePlaceController {
    private final FavoritePlaceService favoritePlaceService;
    /**
     * Update favorite place name for user
     *
     * @param favoritePlaceDto - dto for FavoritePlace entity
     * @return FavoritePlaceDto instance
     * @author Zakhar Skaletskyi
     */
    @PutMapping
    public ResponseEntity<FavoritePlaceDto> update(@Valid @RequestBody FavoritePlaceDto favoritePlaceDto) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService.update(favoritePlaceDto));
    }
    /**
     * Find all favorite places by user email
     *
     * @param  email - user's email
     * @return list of dto
     * @author Zakhar Skaletskyi
     */

    @GetMapping
    public ResponseEntity<List<FavoritePlaceDto>>findAllByUserEmail(@Valid @RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService.findAllByUserEmail(email));

    }
    /**
     * Delete favorite place by place id and user email
     *
     * @param  favoritePlaceDto - dto for FavoritePlace entity
     * @author Zakhar Skaletskyi
     */
    @DeleteMapping
    public ResponseEntity deleteByPlaceIdAndUserEmail(@Valid @RequestBody FavoritePlaceDto favoritePlaceDto) {
        favoritePlaceService.deleteByPlaceIdAndUserEmail(favoritePlaceDto);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
}



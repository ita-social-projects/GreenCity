package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.favoriteplace.FavoritePlaceDto;
import greencity.dto.favoriteplace.FavoritePlaceVO;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.dto.place.PlaceVO;
import greencity.dto.user.UserVO;
import greencity.service.FavoritePlaceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import jakarta.validation.Valid;
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
     * Update {@link FavoritePlaceVO} name for {@link UserVO}. Parameter principal
     * are ignored because Spring automatically provide the Principal object.
     *
     * @param favoritePlaceDto - dto for {@link FavoritePlaceVO} entity
     * @param principal        - Principal with user email
     * @return {@link FavoritePlaceDto} instance
     * @author Zakhar Skaletskyi
     */
    @ApiOperation(value = "Update user favorite place.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = FavoritePlaceDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @PutMapping
    public ResponseEntity<FavoritePlaceDto> update(@Valid @RequestBody FavoritePlaceDto favoritePlaceDto,
        @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .update(favoritePlaceDto, principal.getName()));
    }

    /**
     * Find all {@link FavoritePlaceVO} by {@link UserVO} email. Parameter principal
     * are ignored because Spring automatically provide the Principal object .
     *
     * @param principal - Principal with {@link UserVO} email
     * @return list of {@link FavoritePlaceDto}
     * @author Zakhar Skaletskyi
     */
    @ApiOperation(value = "Get list favorite places by user email")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    public ResponseEntity<List<PlaceByBoundsDto>> findAllByUserEmail(@ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService.findAllByUserEmail(principal.getName()));
    }

    /**
     * Delete {@link FavoritePlaceVO} by {@link UserVO} email and {@link PlaceVO} id
     * Parameter principal are ignored because Spring automatically provide the
     * Principal object.
     *
     * @param placeId   - {@link PlaceVO} id
     * @param principal - Principal with {@link UserVO} email
     * @return id of deleted {@link FavoritePlaceVO}
     * @author Zakhar Skaletskyi
     */
    @ApiOperation(value = "Delete favorite place by userEmail")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = Long.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{placeId}")
    public ResponseEntity<Long> deleteByUserEmailAndPlaceId(@PathVariable Long placeId,
        @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .deleteByUserEmailAndPlaceId(placeId, principal.getName()));
    }

    /**
     * Controller to get {@link FavoritePlaceVO} coordinates, id and name. Parameter
     * principal are ignored because Spring automatically provide the Principal
     * object.
     *
     * @param placeId   - {@link PlaceVO} id
     * @param principal - Principal with {@link UserVO} email
     * @return info about {@link PlaceVO} with name from {@link FavoritePlaceVO}
     * @author Zakhar Skaletskyi
     */
    @ApiOperation(value = "Get favoritePlace with Coordinate")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PlaceByBoundsDto.class),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/favorite/{placeId}")
    public ResponseEntity<PlaceByBoundsDto> getFavoritePlaceWithCoordinate(@PathVariable Long placeId,
        @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(favoritePlaceService
            .getFavoritePlaceWithLocation(placeId, principal.getName()));
    }
}

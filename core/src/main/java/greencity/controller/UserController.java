package greencity.controller;

import greencity.constant.HttpStatuses;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.UpdateUserDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserVO;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Method to find {@link UserCityDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserCityDto}.
     */
    @Operation(summary = "View a list of user cities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}/cities")
    public ResponseEntity<UserCityDto> findAllUsersCities(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsersCities(userId));
    }

    /**
     * Method to find {@link UserLocationDto} by user id.
     *
     * @param userId id of the user
     * @return {@link UserLocationDto}.
     */
    @Operation(summary = "View user location")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}/location")
    public ResponseEntity<UserLocationDto> findUserLocationByUserId(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findUserLocationDtoByUserId(userId));
    }

    /**
     * Method to update user location by user id.
     *
     * @param userId                id of the user
     * @param userProfileDtoRequest contains location data
     */
    @Operation(summary = "Update user location")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> setLocationForUser(
        @PathVariable(name = "id") Long userId,
        @RequestBody UserProfileDtoRequest userProfileDtoRequest) {
        userService.setLocationForUser(userId, userProfileDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Get all user's friends ids by user id.
     *
     * @param userId id of the user.
     * @return list of friends ids.
     */
    @Operation(summary = "Get all user's friends ids by user id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}/all-friends")
    public ResponseEntity<List<Long>> getAllUserFriendsIds(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getAllUserFriendsIds(userId));
    }

    /**
     * Get all user friends ids as a page.
     *
     * @param userId   id of the user.
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    @Operation(summary = "Get all user friends ids {@link UserVO} as a page")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}/friends")
    public ResponseEntity<Page<Long>> getAllUserFriendsIds(@PathVariable("id") Long userId, Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUserFriendsIds(userId, pageable));
    }

    /**
     * Get top 6 friends ids with the highest rating.
     *
     * @param userId - {@link UserVO}'s id
     * @return {@link List} of friends ids
     */
    @Operation(summary = "Get top 6 friends ids with the highest rating")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @GetMapping("/{id}/top-friends")
    public ResponseEntity<List<Long>> getSixFriendsIdsWithTheHighestRating(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getSixFriendsIdsWithTheHighestRating(userId));
    }

    /**
     * Increase user rating by amount specified in {@link UserAddRatingDto}.
     *
     * @param userAddRatingDto contains rating data.
     */
    @Operation(summary = "Increase user rating")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PatchMapping("/rating")
    public ResponseEntity<Void> increaseUserRating(
        @RequestBody UserAddRatingDto userAddRatingDto) {
        userService.increaseUserRating(userAddRatingDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to synchronize GreenCity user entity with GreenCityUser entity. Used
     * by {@link greencity.client.UserRemoteClient} as remote endpoint.
     *
     */
    @Operation(summary = "Updates common GreenCity and GreenCityUser common users' fields.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED)))
    })
    @PatchMapping("/update")
    public ResponseEntity<Boolean> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        return ResponseEntity.ok(userService.update(updateUserDto));
    }

    /**
     * Method to synchronize GreenCityUser's new user entity with GreenCity entity.
     * Used by GreenCityRemoteClient on the GreenCityUser microservice as a remote
     * endpoint to create a new user.
     *
     */
    @Operation(summary = "Creates GreenCity user when it is created on GreenCityUser microservice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT,
            content = @Content(examples = @ExampleObject(HttpStatuses.CONFLICT)))
    })
    @PostMapping("/create")
    public ResponseEntity<Boolean> createUser(@RequestBody CreateGreenCityUserDto createUserDto) {
        return ResponseEntity.ok(userService.createUser(createUserDto));
    }
}

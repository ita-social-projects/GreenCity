package greencity.controller;

import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.GreenCityUserProfileDtoResponse;
import greencity.dto.user.UpdateUserCredoDto;
import greencity.dto.user.UserAddRatingDto;
import greencity.dto.user.UserAddRatingExternalDto;
import greencity.dto.user.UserCityDto;
import greencity.dto.user.UserProfileDtoRequest;
import greencity.dto.user.UserVO;
import greencity.dto.user.CreateGreenCityUserDto;
import greencity.enums.UserStatus;
import greencity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/{id}/cities")
    public ResponseEntity<UserCityDto> findAllUsersCities(@PathVariable(name = "id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsersCities(userId));
    }

    /**
     * For external services usage. Method to find {@link UserCityDto} by user
     * email.
     *
     * @param email user's email
     * @return {@link UserCityDto}.
     */
    @Operation(summary = "View a list of user cities", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/user/cities")
    public ResponseEntity<UserCityDto> findAllUsersCities(@RequestParam String email) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findAllUsersCities(email));
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
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
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
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> setLocationForUser(
        @PathVariable(name = "id") Long userId,
        @RequestBody UserProfileDtoRequest userProfileDtoRequest) {
        userService.setLocationForUser(userId, userProfileDtoRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * For external services usage. Method to update user location by user email.
     *
     * @param email                 user's email
     * @param userProfileDtoRequest contains location data
     */
    @Operation(summary = "Update user location", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PatchMapping("/user/location")
    public ResponseEntity<Void> setLocationForUser(
        @RequestParam String email,
        @RequestBody UserProfileDtoRequest userProfileDtoRequest) {
        userService.setLocationForUser(email, userProfileDtoRequest);
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
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/{id}/all-friends")
    public ResponseEntity<List<Long>> getAllUserFriendsIds(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getAllUserFriendsIds(userId));
    }

    /**
     * For external services usage. Get all user's friends ids by user email.
     *
     * @param email user's email
     * @return list of friends ids.
     */
    @Operation(summary = "Get all user's friends ids by user email", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/user/all-friends")
    public ResponseEntity<List<Long>> getAllUserFriendsIds(@RequestParam String email) {
        return ResponseEntity.ok(userService.getAllUserFriendsIds(email));
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
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/{id}/friends")
    public ResponseEntity<PageableAdvancedDto<Long>> getAllUserFriendsIds(@PathVariable("id") Long userId,
        Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUserFriendsIds(userId, pageable));
    }

    /**
     * For external services usage. Get all user friends ids as a page.
     *
     * @param email    email of the user.
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    @Operation(summary = "Get all user friends ids as a page", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/user/friends")
    public ResponseEntity<PageableAdvancedDto<Long>> getAllUserFriendsIds(@RequestParam String email,
        Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUserFriendsIds(email, pageable));
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
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/{id}/top-friends")
    public ResponseEntity<List<Long>> getSixFriendsIdsWithTheHighestRating(@PathVariable("id") Long userId) {
        return ResponseEntity.ok(userService.getSixFriendsIdsWithTheHighestRating(userId));
    }

    /**
     * For external services usage. Get top 6 friends ids with the highest rating.
     *
     * @param email - {@link UserVO}'s email
     * @return {@link List} of friends ids
     */
    @Operation(summary = "Get top 6 friends ids with the highest rating", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @GetMapping("/user/top-friends")
    public ResponseEntity<List<Long>> getSixFriendsIdsWithTheHighestRating(@RequestParam String email) {
        return ResponseEntity.ok(userService.getSixFriendsIdsWithTheHighestRating(email));
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
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PatchMapping("/rating")
    public ResponseEntity<Void> increaseUserRating(
        @RequestBody UserAddRatingDto userAddRatingDto) {
        userService.increaseUserRating(userAddRatingDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * For external services usage. Increase user rating by amount specified in
     * {@link UserAddRatingDto}.
     *
     * @param userAddRatingDto contains rating data.
     */
    @Operation(summary = "Increase user rating", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN)))
    })
    @PatchMapping("/user-rating")
    public ResponseEntity<Void> increaseUserRating(
        @RequestBody UserAddRatingExternalDto userAddRatingDto) {
        userService.increaseUserRating(userAddRatingDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to update user credo by user id.
     *
     * @param updateUserCredoDto containing update information.
     */
    @Operation(summary = "Update user credo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PatchMapping("/credo")
    public ResponseEntity<Void> updateUserCredoByUserId(@RequestBody UpdateUserCredoDto updateUserCredoDto) {
        userService.updateUserCredo(updateUserCredoDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to synchronize GreenCityUser's new user entity with GreenCity entity.
     * Used by GreenCityRemoteClient on the GreenCityUser microservice as a remote
     * endpoint to create a new user.
     */
    @Operation(summary = "Creates GreenCity user when it is created on GreenCityUser microservice")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = HttpStatuses.CREATED),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "409", description = HttpStatuses.CONFLICT,
            content = @Content(examples = @ExampleObject(HttpStatuses.CONFLICT)))
    })
    @PostMapping("/create")
    public ResponseEntity<Boolean> createUser(@RequestBody CreateGreenCityUserDto createUserDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(createUserDto));
    }

    /**
     * For external services usage. Method to update user's picture path. Used by
     * GreenCityRemoteClient on the GreenCityUser microservice as a remote endpoint.
     */
    @Operation(summary = "Updates user's picture path", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PutMapping("/user/picturePath")
    public ResponseEntity<Void> updatePicturePath(@RequestParam String email,
        @RequestParam(name = "profilePicturePath") String profilePicturePath) {
        userService.updateUserProfilePicture(email, profilePicturePath);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for updating user's name.
     *
     * @param userId   - {@link Long} of user's id.
     * @param userName - new user's name.
     */
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PatchMapping("/{userId}/name")
    public ResponseEntity<Void> updateUserName(@PathVariable Long userId, @RequestParam String userName) {
        userService.updateUserName(userId, userName);
        return ResponseEntity.ok().build();
    }

    /**
     * For external services usage. Method for updating user's name.
     *
     * @param email    - user's email.
     * @param userName - new user's name.
     */
    @Operation(summary = "Updates user's name", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @PatchMapping("/user/name")
    public ResponseEntity<Void> updateUserName(@RequestParam String email, @RequestParam String userName) {
        userService.updateUserName(email, userName);
        return ResponseEntity.ok().build();
    }

    /**
     * Method to find list of {@link GreenCityUserProfileDtoResponse} containing
     * information about user.
     *
     * @param userIds ids of users for whom to fetch the data
     * @return list of {@link GreenCityUserProfileDtoResponse} containing
     *         information about user
     */
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/profiles")
    public ResponseEntity<List<GreenCityUserProfileDtoResponse>> findGreenCityUserProfilesByUserIds(
        @RequestParam List<Long> userIds) {
        return ResponseEntity.ok(userService.findGreenCityUserProfilesByUserIds(userIds));
    }

    /**
     * For external services usage. Method to find list of
     * {@link GreenCityUserProfileDtoResponse} containing information about user.
     *
     * @param emails emails of users for whom to fetch the data
     * @return list of {@link GreenCityUserProfileDtoResponse} containing
     *         information about user
     */
    @Operation(summary = "Get user profiles by emails", description = "For external services usage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST,
            content = @Content(examples = @ExampleObject(HttpStatuses.BAD_REQUEST))),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED,
            content = @Content(examples = @ExampleObject(HttpStatuses.UNAUTHORIZED))),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN,
            content = @Content(examples = @ExampleObject(HttpStatuses.FORBIDDEN))),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND,
            content = @Content(examples = @ExampleObject(HttpStatuses.NOT_FOUND)))
    })
    @GetMapping("/profiles/external")
    public ResponseEntity<List<GreenCityUserProfileDtoResponse>> findGreenCityUserProfilesByEmails(
        @RequestParam List<String> emails) {
        return ResponseEntity.ok(userService.findGreenCityUserProfilesByEmails(emails));
    }

    /**
     * Change user status.
     *
     * @param currentUser - current user
     * @param userId - target user id
     * @param status - user status
     */
    @Operation(summary = "Change user status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST, content = @Content),
        @ApiResponse(responseCode = "401", description = HttpStatuses.UNAUTHORIZED, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content),
    })
    @PutMapping("/status/{userId}")
    public ResponseEntity<HttpStatus> changeUserStatus(
        @Parameter(hidden = true) @CurrentUser UserVO currentUser,
        @PathVariable Long userId,
        @RequestParam UserStatus status) {
        userService.updateUserStatusById(currentUser, userId, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for getting a {@link List} of {@link String} - reasons for
     * deactivation of the current user.
     *
     * @param id        {@link Long} - user's id.
     * @param currentUser - current user
     * @return {@link List} of {@link String} - reasons for deactivation of the
     *         current user.
     */
    @Operation(summary = "Get list reasons of deactivating the user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
        @ApiResponse(responseCode = "403", description = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/reasons")
    public ResponseEntity<List<String>> getReasonsOfDeactivation(
        @RequestParam("id") Long id, @Parameter(hidden = true) @CurrentUser UserVO currentUser) {
        return ResponseEntity.ok().body(userService.getDeactivationReasons(id, currentUser));
    }

    /**
     * Method to get status of user. Used by GreenCityRemoteClient in other services.
     *
     * @param email user's email
     * @return {@link UserStatus}
     */
    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<UserStatus> getUserStatus(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserStatusByEmail(email));
    }

    /**
     * Method for deleting current authenticated user. Deleted user is still existed in system but with DELETED
     * status and can be restored.
     *
     * @return {@link ResponseEntity}
     */
    @Operation(summary = "Delete current user.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK, content = @Content),
        @ApiResponse(responseCode = "404", description = HttpStatuses.NOT_FOUND, content = @Content)
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteUser(@Parameter(hidden = true) @CurrentUser UserVO user) {
        userService.deleteUserByEmail(user.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * Counts all users by user {@link UserStatus} ACTIVATED.
     *
     * @return amount of users with {@link UserStatus} ACTIVATED.
     */
    @Operation(summary = "Get all activated users amount")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = HttpStatuses.OK),
        @ApiResponse(responseCode = "400", description = HttpStatuses.BAD_REQUEST),
    })
    @GetMapping("/activatedUsersAmount")
    public ResponseEntity<Long> getActivatedUsersAmount() {
        return ResponseEntity.ok().body(userService.getActivatedUsersAmount());
    }
}

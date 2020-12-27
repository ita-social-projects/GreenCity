package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUserId;
import greencity.annotations.ImageValidation;
import greencity.constant.HttpStatuses;
import greencity.constant.SwaggerExampleModel;
import greencity.dto.PageableDto;
import greencity.dto.filter.FilterUserDto;
import greencity.dto.friends.SixFriendsPageResponceDto;
import greencity.dto.goal.CustomGoalResponseDto;
import greencity.dto.user.*;
import greencity.enums.EmailNotification;
import greencity.enums.UserStatus;
import greencity.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static greencity.constant.AppConstant.AUTHORIZATION;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    /**
     * The method which update user status. Parameter principal are ignored because
     * Spring automatically provide the Principal object.
     *
     * @param userStatusDto - dto with updated filed.
     * @return {@link UserStatusDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Update status of user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserStatus.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("status")
    public ResponseEntity<UserStatusDto> updateStatus(
        @Valid @RequestBody UserStatusDto userStatusDto, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateStatus(
                    userStatusDto.getId(), userStatusDto.getUserStatus(), principal.getName()));
    }

    /**
     * The method which update user role. Parameter principal are ignored because
     * Spring automatically provide the Principal object.
     *
     * @param userRoleDto - dto with updated field.
     * @return {@link UserRoleDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Update role of user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserRoleDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping("role")
    public ResponseEntity<UserRoleDto> updateRole(
        @Valid @RequestBody UserRoleDto userRoleDto, @ApiIgnore Principal principal) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(
                userService.updateRole(
                    userRoleDto.getId(), userRoleDto.getRole(), principal.getName()));
    }

    /**
     * The method which return list of users by page. Parameter pageable ignored
     * because swagger ui shows the wrong params, instead they are explained in the
     * {@link ApiPageable}.
     *
     * @param pageable - pageable configuration.
     * @return list of {@link PageableDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Get users by page")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @GetMapping("all")
    public ResponseEntity<PageableDto<UserForListDto>> getAllUsers(@ApiIgnore Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByPage(pageable));
    }

    /**
     * The method which return array of existing roles.
     *
     * @return {@link RoleDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Get all available roles")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = RoleDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("roles")
    public ResponseEntity<RoleDto> getRoles() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getRoles());
    }

    /**
     * The method which return array of existing {@link EmailNotification}.
     *
     * @return {@link EmailNotification} array
     * @author Nazar Vladyka
     */
    @ApiOperation(value = "Get all available email notifications statuses")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = EmailNotification[].class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST)
    })
    @GetMapping("emailNotifications")
    public ResponseEntity<List<EmailNotification>> getEmailNotifications() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getEmailNotificationsStatuses());
    }

    /**
     * The method which return list of users by filter. Parameter pageable ignored
     * because swagger ui shows the wrong params, instead they are explained in the
     * {@link ApiPageable}.
     *
     * @param filterUserDto dto which contains fields with filter criteria.
     * @param pageable      - pageable configuration.
     * @return {@link PageableDto}
     * @author Rostyslav Khasanov
     */
    @ApiOperation(value = "Filter all user by search criteria")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = PageableDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @ApiPageable
    @PostMapping("filter")
    public ResponseEntity<PageableDto<UserForListDto>> getUsersByFilter(
        @ApiIgnore Pageable pageable, @RequestBody FilterUserDto filterUserDto) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersByFilter(filterUserDto, pageable));
    }

    /**
     * Get {@link UserVO} dto by principal (email) from access token.
     *
     * @return {@link UserUpdateDto}.
     * @author Nazar Stasyuk
     */
    @ApiOperation(value = "Get User dto by principal (email) from access token")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = UserUpdateDto.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping
    public ResponseEntity<UserUpdateDto> getUserByPrincipal(@ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUserUpdateDtoByEmail(email));
    }

    /**
     * Update {@link UserVO}.
     *
     * @return {@link ResponseEntity}.
     * @author Nazar Stasyuk
     */
    @ApiOperation(value = "Update User")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping
    public ResponseEntity<UserUpdateDto> updateUser(@Valid @RequestBody UserUpdateDto dto,
        @ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(dto, email));
    }

    /**
     * Method returns list of available (not ACTIVE) custom goals for user.
     *
     * @return {@link ResponseEntity}.
     * @author Vitalii Skolozdra
     */
    @ApiOperation(value = "Get available custom goals for current user.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/customGoals/available")
    public ResponseEntity<List<CustomGoalResponseDto>> getAvailableCustomGoals(
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId,
        @ApiIgnore @RequestHeader(AUTHORIZATION) String accessToken) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getAvailableCustomGoals(userId, accessToken));
    }

    /**
     * Counts all users by user {@link UserStatus} ACTIVATED.
     *
     * @return amount of users with {@link UserStatus} ACTIVATED.
     * @author Shevtsiv Rostyslav
     */
    @ApiOperation(value = "Get all activated users amount")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK, response = Long.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @GetMapping("/activatedUsersAmount")
    public ResponseEntity<Long> getActivatedUsersAmount() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.getActivatedUsersAmount());
    }

    /**
     * Update user profile picture {@link UserVO}.
     *
     * @return {@link ResponseEntity}.
     * @author Datsko Marian
     */
    @ApiOperation(value = "Update user profile picture")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping(path = "/profilePicture",
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<HttpStatus> updateUserProfilePicture(
        @ApiParam(value = SwaggerExampleModel.USER_PROFILE_PICTURE_DTO,
            required = true) @RequestPart UserProfilePictureDto userProfilePictureDto,
        @ApiParam(value = "Profile picture") @ImageValidation @RequestPart(required = false) MultipartFile image,
        @ApiIgnore @AuthenticationPrincipal Principal principal,
        @ApiIgnore @RequestHeader(AUTHORIZATION) String accessToken) {
        String email = principal.getName();
        userService.updateUserProfilePicture(image, email, userProfilePictureDto, accessToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Delete user profile picture {@link UserVO}.
     *
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Delete user profile picture")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @PatchMapping(path = "/deleteProfilePicture")
    public ResponseEntity<HttpStatus> deleteUserProfilePicture(
        @ApiIgnore @AuthenticationPrincipal Principal principal) {
        String email = principal.getName();
        userService.deleteUserProfilePicture(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for deleting user friend.
     *
     * @param friendId id user friend.
     * @param userId   id current user.
     * @author Marian Datsko
     */
    @ApiOperation(value = "Delete user friend")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @DeleteMapping("/{userId}/userFriend/{friendId}")
    public ResponseEntity<Object> deleteUserFriend(
        @ApiParam("Id friend of current user. Cannot be empty.") @PathVariable Long friendId,
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        userService.deleteUserFriendById(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for add new user friend.
     *
     * @param friendId id user friend.
     * @param userId   id current user.
     * @author Marian Datsko
     */
    @ApiOperation(value = "Add new user friend")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PostMapping("/{userId}/userFriend/{friendId}")
    public ResponseEntity<Object> addNewFriend(
        @ApiParam("Id friend of current user. Cannot be empty.") @PathVariable Long friendId,
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        userService.addNewFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method returns list profile picture with the highest rating.
     *
     * @return {@link ResponseEntity}.
     * @author Datsko Marian + Oleh Bilonizhka
     */
    @ApiOperation(value = "Get six friends profile picture with the highest rating")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/sixUserFriends/")
    public ResponseEntity<SixFriendsPageResponceDto> getSixFriendsWithTheHighestRating(
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getSixFriendsWithTheHighestRatingPaged(userId));
    }

    /**
     * The method finds {@link RecommendedFriendDto} for the current userId.
     *
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Find recommended friends")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/recommendedFriends/")
    @ApiPageable
    public ResponseEntity<PageableDto<RecommendedFriendDto>> findUsersRecommendedFriends(
        @ApiIgnore Pageable page,
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.findUsersRecommendedFriends(page, userId));
    }

    /**
     * The method finds {@link RecommendedFriendDto} for the current userId.
     *
     * @return {@link ResponseEntity}.
     */
    @ApiOperation(value = "Find all friends")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/friends/")
    @ApiPageable
    public ResponseEntity<PageableDto<RecommendedFriendDto>> findAllUsersFriends(
        @ApiIgnore Pageable page,
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.findAllUsersFriends(page, userId));
    }

    /**
     * Method for save user profile information {@link UserProfileDtoResponse}.
     *
     * @param userProfileDtoRequest - dto for {@link UserVO} entity.
     * @return dto {@link UserProfileDtoResponse} instance.
     * @author Marian Datsko.
     */
    @ApiOperation(value = "Save user profile information")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = HttpStatuses.CREATED,
            response = UserProfileDtoResponse.class),
        @ApiResponse(code = 303, message = HttpStatuses.SEE_OTHER),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN),
    })
    @PutMapping(path = "/profile")
    public ResponseEntity<UserProfileDtoResponse> save(
        @ApiParam(required = true) @RequestBody @Valid UserProfileDtoRequest userProfileDtoRequest,
        @ApiIgnore Principal principal, @ApiIgnore @RequestHeader(AUTHORIZATION) String accessToken) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            userService.saveUserProfile(userProfileDtoRequest, principal.getName(), accessToken));
    }

    /**
     * Method returns user profile information.
     *
     * @return {@link UserProfileDtoResponse}.
     * @author Datsko Marian
     */
    @ApiOperation(value = "Get user profile information by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/profile/")
    public ResponseEntity<UserProfileDtoResponse> getUserProfileInformation(
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserProfileInformation(userId));
    }

    /**
     * The method checks by id if a {@link UserVO} is online.
     *
     * @return {@link ResponseEntity}.
     * @author Zhurakovskyi Yurii
     */
    @ApiOperation(value = "Check by id if the user is online")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("isOnline/{userId}/")
    public ResponseEntity<Boolean> checkIfTheUserIsOnline(
        @ApiParam("Id of the user. Cannot be empty.") @PathVariable Long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.checkIfTheUserIsOnline(userId));
    }

    /**
     * Method returns user profile statistics.
     *
     * @return {@link UserProfileStatisticsDto}.
     * @author Datsko Marian
     */
    @ApiOperation(value = "Get user profile statistics by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 403, message = HttpStatuses.FORBIDDEN)
    })
    @GetMapping("/{userId}/profileStatistics/")
    public ResponseEntity<UserProfileStatisticsDto> getUserProfileStatistics(
        @ApiParam("Id of current user. Cannot be empty.") @PathVariable @CurrentUserId Long userId,
        @ApiIgnore @RequestHeader(AUTHORIZATION) String accessToken) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(userService.getUserProfileStatistics(userId, accessToken));
    }

    /**
     * The method get {@link UserVO}s with online status for the current user-id.
     *
     * @return {@link UserAndFriendsWithOnlineStatusDto}.
     * @author Zhurakovskyi Yurii
     */
    @MessageMapping("/userAndSixFriendsWithOnlineStatus")
    @SendTo("/topic/sixUsersOnlineStatus")
    public UserAndFriendsWithOnlineStatusDto getUserAndSixFriendsWithOnlineStatus(
        Long userId) {
        return userService.getUserAndSixFriendsWithOnlineStatus(userId);
    }

    /**
     * The method get all {@link UserVO}s with online status for the current
     * user-id.
     *
     * @return {@link UserAndAllFriendsWithOnlineStatusDto}.
     * @author Zhurakovskyi Yurii
     */
    @MessageMapping("/userAndAllFriendsWithOnlineStatus")
    @SendTo("/topic/userAndAllFriendsOnlineStatus")
    public UserAndAllFriendsWithOnlineStatusDto getUserAndAllFriendsWithOnlineStatus(
        Long userId, Pageable pageable) {
        return userService.getAllFriendsWithTheOnlineStatus(userId, pageable);
    }
}

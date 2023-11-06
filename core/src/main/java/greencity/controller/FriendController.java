package greencity.controller;

import greencity.annotations.ApiPageable;
import greencity.annotations.CurrentUser;
import greencity.constant.HttpStatuses;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.enums.RecommendedFriendsType;
import greencity.service.FriendService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/friends")
public class FriendController {
    private final FriendService friendService;

    /**
     * Method for deleting user's friend.
     *
     * @param friendId id user friend.
     * @param userVO   {@link UserVO} user.
     * @author Marian Datsko
     */
    @ApiOperation(value = "Delete user's friend")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}")
    public ResponseEntity<ResponseEntity.BodyBuilder> deleteUserFriend(
        @ApiParam("Id friend of current user. Cannot be empty.") @PathVariable long friendId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        friendService.deleteUserFriendById(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for add new user friend.
     *
     * @param friendId id user friend.
     * @param userVO   {@link UserVO} user.
     * @author Marian Datsko
     */
    @ApiOperation(value = "Add new user friend")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @PostMapping("/{friendId}")
    public ResponseEntity<ResponseEntity.BodyBuilder> addNewFriend(
        @ApiParam("Id friend of current user. Cannot be empty.") @PathVariable long friendId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        friendService.addNewFriend(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for accepting friend request from user.
     *
     * @param friendId id user friend.
     * @param userVO   {@link UserVO} user.
     */
    @ApiOperation(value = "Accept friend request")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @PatchMapping("/{friendId}/acceptFriend")
    public ResponseEntity<ResponseEntity.BodyBuilder> acceptFriendRequest(
        @ApiParam("Friend's id. Cannot be empty.") @PathVariable long friendId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        friendService.acceptFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method for declining friend request from user. Change status from REQUEST to
     * REJECTED.
     *
     * @param friendId id user friend.
     * @param userVO   {@link UserVO} user.
     */
    @ApiOperation(value = "Decline friend request")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND),
    })
    @PatchMapping("/{friendId}/declineFriend")
    public ResponseEntity<Object> declineFriendRequest(
        @ApiParam("Friend's id. Cannot be empty.") @PathVariable long friendId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        friendService.declineFriendRequest(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Method to find all user friends by userId.
     *
     * @param userId user id.
     *
     * @return {@link PageableDto} of {@link UserManagementDto}.
     * @author Orest Mamchuk
     */
    @ApiOperation(value = "Get all user friends")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/user/{userId}")
    @ApiPageable
    public ResponseEntity<PageableDto<UserManagementDto>> findUserFriendsByUserId(
        @ApiIgnore @PageableDefault Pageable page,
        @PathVariable long userId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(friendService.findUserFriendsByUserId(page, userId));
    }

    /**
     * Method to find {@link UserFriendDto}s that are not friend for current
     * user(except current user).
     *
     * @param userVO user.
     * @param name   filtering name.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     */
    @ApiOperation(value = "Find all users that are not friend for current users")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/not-friends-yet")
    @ApiPageable
    public ResponseEntity<PageableDto<UserFriendDto>> findAllUsersExceptMainUserAndUsersFriend(
        @ApiIgnore @PageableDefault Pageable page,
        @ApiIgnore @CurrentUser UserVO userVO,
        @RequestParam(required = false) @Nullable String name) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(friendService.findAllUsersExceptMainUserAndUsersFriend(userVO.getId(), name, page));
    }

    /**
     * Method to find recommended friends for current user by type.
     *
     * @param userVO user.
     * @param type   type to find recommended friends Supported values:
     *               "FRIENDS_OF_FRIENDS", "HABITS".
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     */
    @ApiOperation(value = "Find recommended friends by type")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
    })
    @GetMapping("/recommended-friends")
    @ApiPageable
    public ResponseEntity<PageableDto<UserFriendDto>> findRecommendedFriends(
        @ApiIgnore Pageable page,
        @RequestParam(required = false) RecommendedFriendsType type,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(friendService.findRecommendedFriends(userVO.getId(), type, page));
    }

    /**
     * Method to find {@link UserFriendDto}s which sent request to current user.
     *
     * @param userVO user.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     */
    @ApiOperation(value = "Find user's requests")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @GetMapping("/friendRequests")
    @ApiPageable
    public ResponseEntity<PageableDto<UserFriendDto>> getAllUserFriendsRequests(
        @ApiIgnore Pageable page,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(friendService.getAllUserFriendRequests(userVO.getId(), page));
    }

    /**
     * The method returns friends for the current user.
     *
     * @param userVO user.
     * @param name   filtering name.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     */
    @ApiOperation(value = "Find all friends")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping
    @ApiPageable
    public ResponseEntity<PageableDto<UserFriendDto>> findAllFriendsOfUser(
        @ApiIgnore Pageable page,
        @RequestParam(required = false) @Nullable String name,
        @ApiIgnore @CurrentUser UserVO userVO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(friendService.findAllFriendsOfUser(userVO.getId(), name, page));
    }

    /**
     * The method returns mutual friends for the current user.
     *
     * @param friendId The id of the friend for whom you want to find mutual
     *                 friends.
     * @param userVO   current user.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     */
    @ApiOperation(value = "Get all mutual friends for current user")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED)
    })
    @GetMapping("/mutual-friends")
    @ApiPageable
    public ResponseEntity<PageableDto<UserFriendDto>> getMutualFriends(
        @RequestParam Long friendId,
        @ApiIgnore @CurrentUser UserVO userVO,
        @ApiIgnore Pageable page) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(friendService.getMutualFriends(userVO.getId(), friendId, page));
    }

    /**
     * Method for canceling a request that the current user has sent before to user
     * with friendId.
     *
     * @param friendId id user friend.
     * @param userVO   {@link UserVO} user.
     * @author Lilia Mokhnatska
     */
    @ApiOperation(value = "Delete user's request to friend")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = HttpStatuses.OK),
        @ApiResponse(code = 400, message = HttpStatuses.BAD_REQUEST),
        @ApiResponse(code = 401, message = HttpStatuses.UNAUTHORIZED),
        @ApiResponse(code = 404, message = HttpStatuses.NOT_FOUND)
    })
    @DeleteMapping("/{friendId}/cancelRequest")
    public ResponseEntity<ResponseEntity.BodyBuilder> cancelRequest(
        @ApiParam("Id friend of current user. Cannot be empty.") @PathVariable long friendId,
        @ApiIgnore @CurrentUser UserVO userVO) {
        friendService.deleteRequestOfCurrentUserToFriend(userVO.getId(), friendId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}

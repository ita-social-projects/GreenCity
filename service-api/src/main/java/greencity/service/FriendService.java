package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.RecommendedFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

import java.util.List;

public interface FriendService {
    /**
     * Delete user's friend by friendId.
     *
     * @param userId   user id
     * @param friendId friend id
     * @author Marian Datsko
     */
    void deleteUserFriendById(long userId, long friendId);

    /**
     * Add new friend by friendId.
     *
     * @param userId   user id
     * @param friendId friend id
     * @author Marian Datsko
     */
    void addNewFriend(long userId, long friendId);

    /**
     * Accept friend request {@link UserVO}.
     *
     * @param userId   user id
     * @param friendId friend id
     */
    void acceptFriendRequest(long userId, long friendId);

    /**
     * Decline friend request {@link UserVO}.
     *
     * @param userId   user id
     * @param friendId friend id
     */
    void declineFriendRequest(long userId, long friendId);

    /**
     * Method that finds user's friends by userId.
     *
     * @param userId user id
     *
     * @return {@link List} of {@link UserManagementDto} instances.
     */
    List<UserManagementDto> findUserFriendsByUserId(long userId);

    /**
     * Method find all users except current user and his friends.
     *
     * @param userId   user id.
     * @param name     filtering name.
     * @param pageable the information about pagination and sorting for the result,
     *                 must not be null.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     *
     * @author Stepan Omeliukh
     */
    PageableDto<UserFriendDto> findAllUsersExceptMainUserAndUsersFriend(long userId, @Nullable String name,
        Pageable pageable);

    /**
     * Method to find {@link UserFriendDto}s which sent request to user with userId.
     *
     * @param pageable the information about pagination and sorting for the result,
     *                 must not be null.
     * @param userId   user id.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     */
    PageableDto<UserFriendDto> getAllUserFriendRequests(long userId, Pageable pageable);

    /**
     * Method that finds all user's friends.
     *
     * @param userId   user id.
     * @param name     filtering name.
     * @param pageable pageable, must not be null.
     *
     * @return {@link PageableDto} of {@link RecommendedFriendDto} instances.
     */
    PageableDto<UserFriendDto> findAllFriendsOfUser(long userId, @Nullable String name, Pageable pageable);

    PageableDto<UserFriendDto> findAllUsersByFriendsOfFriends(long userId, @Nullable String name,
                                                                        Pageable pageable);
}

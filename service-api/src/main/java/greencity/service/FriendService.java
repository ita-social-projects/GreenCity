package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendService {
    /**
     * Delete user's friend by friendId.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    void deleteUserFriendById(Long userId, Long friendId);

    /**
     * Add new friend by friendId.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    void addNewFriend(Long userId, Long friendId);

    /**
     * Accept friend request {@link UserVO}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     */
    void acceptFriendRequest(Long userId, Long friendId);

    /**
     * Decline friend request {@link UserVO}.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     */
    void declineFriendRequest(Long userId, Long friendId);

    /**
     * Method that finds user's friends by userId.
     *
     * @param userId {@link Long}
     *
     * @return {@link List} of {@link UserManagementDto} instances.
     */
    List<UserManagementDto> findUserFriendsByUserId(Long userId);

    /**
     * Method find all users except current user and his friends.
     *
     * @param pageable the information about pagination and sorting for the result.
     * @param userId   user id.
     * @param name     filtering name.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     *
     * @author Stepan Omeliukh
     */
    PageableDto<UserFriendDto> findAllUsersExceptMainUserAndUsersFriend(Pageable pageable, Long userId, String name);

    /**
     * Method to find {@link UserFriendDto}s which sent request to user with userId.
     *
     * @param pageable the information about pagination and sorting for the result.
     * @param userId   user id.
     *
     * @return {@link PageableDto} of {@link UserFriendDto}.
     */
    PageableDto<UserFriendDto> getAllUserFriendRequests(Long userId, Pageable pageable);
}

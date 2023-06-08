package greencity.service;

import greencity.dto.user.UserVO;

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
}

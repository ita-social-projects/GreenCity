package greencity.service;

public interface FriendService {
    /**
     * Delete user's friend by friendId.
     *
     * @param userId   {@link Long}
     * @param friendId {@link Long}
     * @author Marian Datsko
     */
    void deleteUserFriendById(Long userId, Long friendId);
}

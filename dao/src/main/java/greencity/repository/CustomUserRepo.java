package greencity.repository;

import greencity.dto.friends.UserFriendDto;
import greencity.entity.User;

import java.util.List;

public interface CustomUserRepo {
    /**
     * Fill {@link List} of {@link User} with count of mutual friends and chat id
     * for current user.
     *
     * @param userId current user's id.
     * @param users  list of users to be filled
     *
     * @return {@link List} of {@link UserFriendDto}.
     */
    List<UserFriendDto> fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(Long userId, List<User> users);
}

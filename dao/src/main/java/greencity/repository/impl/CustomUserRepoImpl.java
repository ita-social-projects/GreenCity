package greencity.repository.impl;

import greencity.dto.friends.UserFriendDto;
import greencity.entity.User;
import greencity.repository.CustomUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class CustomUserRepoImpl implements CustomUserRepo {
    private final EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserFriendDto> fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(Long userId,
        List<User> users) {
        TypedQuery<UserFriendDto> query = entityManager
            .createNamedQuery("User.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser",
                UserFriendDto.class);
        query.setParameter("userId", userId);
        if (users.isEmpty()) {
            query.setParameter("users", List.of(-1));
        } else {
            query.setParameter("users", users);
        }
        return query.getResultList();
    }
}

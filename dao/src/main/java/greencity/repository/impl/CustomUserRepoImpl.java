package greencity.repository.impl;

import greencity.dto.friends.UserFriendDto;
import greencity.entity.User;
import greencity.repository.CustomUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class CustomUserRepoImpl implements CustomUserRepo {
    private final EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserFriendDto> fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(long userId,
        List<User> users) {
        Objects.requireNonNull(users);

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

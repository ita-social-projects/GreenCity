package greencity.repository.impl;

import greencity.client.UserRemoteClient;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserEmailDto;
import greencity.entity.User;
import greencity.repository.CustomUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class CustomUserRepoImpl implements CustomUserRepo {
    private final EntityManager entityManager;
    private final UserRemoteClient userRemoteClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserFriendDto> fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser(long userId,
        List<User> users) {
        Objects.requireNonNull(users);

        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        TypedQuery<UserFriendDto> query = entityManager
            .createNamedQuery("User.fillListOfUserWithCountOfMutualFriendsAndChatIdForCurrentUser",
                UserFriendDto.class);
        query.setParameter("userId", userId);

        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());

        query.setParameter("greencity_users", userIds);

        List<UserFriendDto> resultList = query.getResultList();

        List<UserEmailDto> userEmailDtos = userRemoteClient.findUserEmailsByUserIds(userIds);
        Map<Long, String> userIdToUserEmailMap = userEmailDtos.stream()
            .collect(Collectors.toMap(
                UserEmailDto::userId,
                UserEmailDto::userEmail));

        resultList.forEach(userFriendDto -> {
            String email = userIdToUserEmailMap.get(userFriendDto.getId());
            userFriendDto.setEmail(email);
        });

        Map<Long, UserFriendDto> resultMap = resultList.stream()
            .collect(Collectors.toMap(UserFriendDto::getId, dto -> dto));

        return userIds.stream()
            .map(resultMap::get)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}

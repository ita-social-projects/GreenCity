package greencity.constant;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Class, that contains named queries constants.
 */
@NoArgsConstructor(access = PRIVATE)
public final class RepoQueryConstants {
    public static final String GET_ALL_USERS_EXCEPT_MAIN_USER_AND_FRIENDS = "SELECT *, (SELECT count(*) "
        + "        FROM users_friends uf1 "
        + "        WHERE uf1.user_id in :friends "
        + "          and uf1.friend_id = u.id "
        + "          and uf1.status = 'FRIEND' "
        + "           or "
        + "         uf1.friend_id in :friends "
        + "          and uf1.user_id = u.id "
        + "          and uf1.status = 'FRIEND') as mutualFriends, "
        + "       u.profile_picture           as profilePicturePath, "
        + "       (SELECT p.room_id "
        + "       FROM chat_rooms_participants p"
        + "       WHERE p.participant_id IN (u.id, :userId) "
        + "       GROUP BY p.room_id "
        + "       HAVING COUNT(DISTINCT p.participant_id) = 2 LIMIT 1) as chatId "
        + "FROM users u "
        + "WHERE u.id != :userId "
        + "AND u.id NOT IN :friends AND LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) ";

    public static final String GET_ALL_USER_FRIEND_REQUESTS = "SELECT *, (SELECT count(*) "
        + "        FROM users_friends uf1 "
        + "        WHERE uf1.user_id in :friends "
        + "          and uf1.friend_id = u.id "
        + "          and uf1.status = 'FRIEND' "
        + "           or "
        + "         uf1.friend_id in :friends "
        + "          and uf1.user_id = u.id "
        + "          and uf1.status = 'FRIEND') as mutualFriends, "
        + "       u.profile_picture           as profilePicturePath, "
        + "       (SELECT p.room_id "
        + "       FROM chat_rooms_participants p"
        + "       WHERE p.participant_id IN (u.id, :userId) "
        + "       GROUP BY p.room_id "
        + "       HAVING COUNT(DISTINCT p.participant_id) = 2 LIMIT 1) as chatId "
        + "       FROM users u "
        + "       INNER JOIN users_friends ON u.id = users_friends.user_id "
        + "       WHERE users_friends.friend_id = :userId AND users_friends.status = 'REQUEST' ";

    public static final String FIND_ALL_REGISTRATION_MONTHS =
        "SELECT EXTRACT(MONTH FROM date_of_registration) - 1 as month, count(date_of_registration) FROM users "
            + "WHERE EXTRACT(YEAR from date_of_registration) = EXTRACT(YEAR FROM CURRENT_DATE) "
            + "GROUP BY month";
}

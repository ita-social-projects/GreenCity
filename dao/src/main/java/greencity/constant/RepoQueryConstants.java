package greencity.constant;

import greencity.annotations.Generated;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

/**
 * Class, that contains named queries constants.
 */
@Generated
@NoArgsConstructor(access = PRIVATE)
public final class RepoQueryConstants {
    public static final String FIND_ALL_REGISTRATION_MONTHS =
        "SELECT EXTRACT(MONTH FROM date_of_registration) - 1 as month, count(date_of_registration) FROM users "
            + "WHERE EXTRACT(YEAR from date_of_registration) = EXTRACT(YEAR FROM CURRENT_DATE) "
            + "GROUP BY month";

    public static final String FILL_LIST_OF_USER_WITH_COUNT_OF_MUTUAL_FRIENDS_AND_CHAT_ID_FOR_CURRENT_USER =
        "with current_user_friends as ("
            + "SELECT user_id AS id "
            + "    FROM users_friends "
            + "    WHERE friend_id = :userId AND status = 'FRIEND' "
            + "    UNION "
            + "    SELECT friend_id AS id "
            + "    FROM users_friends "
            + "    WHERE user_id = :userId AND status = 'FRIEND' "
            + ") "
            + "SELECT *, (SELECT count(*) "
            + "        FROM users_friends uf1 "
            + "        WHERE uf1.user_id in (SELECT id FROM current_user_friends) "
            + "          and uf1.friend_id = u.id "
            + "          and uf1.status = 'FRIEND' "
            + "           or "
            + "         uf1.friend_id in (SELECT id FROM current_user_friends) "
            + "          and uf1.user_id = u.id "
            + "          and uf1.status = 'FRIEND') as mutualFriends, "
            + "       u.profile_picture           as profilePicturePath, "
            + "       (SELECT p.room_id "
            + "       FROM chat_rooms_participants p"
            + "       WHERE p.participant_id IN (u.id, :userId) "
            + "       GROUP BY p.room_id "
            + "       HAVING COUNT(DISTINCT p.participant_id) = 2 LIMIT 1) as chatId "
            + " FROM users u "
            + " WHERE u.id IN (:users)";
}

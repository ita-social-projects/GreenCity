package greencity.repository;

import greencity.dto.habit.HabitVO;
import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * Find {@link User} by email.
     *
     * @param email user email.
     * @return {@link User}
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all {@link UserManagementVO}.
     *
     * @param filter   filter parameters
     * @param pageable pagination
     * @return list of all {@link UserManagementVO}
     */
    @Query(" SELECT new greencity.dto.user.UserManagementVO(u.id, u.name, u.email, u.userCredo, u.role, u.userStatus) "
        + " FROM User u ")
    Page<UserManagementVO> findAllManagementVo(Specification<User> filter, Pageable pageable);

    /**
     * Find not 'DEACTIVATED' {@link User} by email.
     *
     * @param email - {@link User}'s email
     * @return found {@link User}
     * @author Vasyl Zhovnir
     */
    @Query("FROM User WHERE email=:email AND userStatus <> 1")
    Optional<User> findNotDeactivatedByEmail(String email);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     * @author Zakhar Skaletskyi
     */
    @Query("SELECT id FROM User WHERE email=:email")
    Optional<Long> findIdByEmail(String email);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link User}'s id
     * @param userLastActivityTime - new {@link User}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.lastActivityTime=:userLastActivityTime WHERE u.id=:userId")
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * Updates user status for a given user.
     *
     * @param userId     - {@link User}'s id
     * @param userStatus {@link String} - string value of user status to set
     */
    @Modifying
    @Transactional
    @Query("UPDATE User SET userStatus = CASE "
        + "WHEN (:userStatus = 'DEACTIVATED') THEN 1 "
        + "WHEN (:userStatus = 'ACTIVATED') THEN 2 "
        + "WHEN (:userStatus = 'CREATED') THEN 3 "
        + "WHEN (:userStatus = 'BLOCKED') THEN 4 "
        + "ELSE 0 END "
        + "WHERE id = :userId")
    void updateUserStatus(Long userId, String userStatus);

    /**
     * Find the last activity time by {@link User}'s id.
     *
     * @param userId - {@link User}'s id
     * @return {@link Date}
     */
    @Query(nativeQuery = true,
        value = "SELECT last_activity_time FROM users WHERE id=:userId")
    Optional<Timestamp> findLastActivityTimeById(Long userId);

    /**
     * Get six friends with the highest rating {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE users.id IN ( "
        + "(SELECT user_id FROM users_friends WHERE friend_id = :userId AND status = 'FRIEND') "
        + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId AND status = 'FRIEND')) "
        + "ORDER BY users.rating DESC LIMIT 6;")
    List<User> getSixFriendsWithTheHighestRating(Long userId);

    /**
     * Updates user rating as event organizer.
     *
     * @param userId {@link User}'s id
     * @param rate   new {@link User}'s rating as event organizer
     * @author Danylo Hlynskyi
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User SET eventOrganizerRating=:rate WHERE id=:userId")
    void updateUserEventOrganizerRating(Long userId, Double rate);

    /**
     * Find user by user id and friend id when their status is Friend.
     *
     * @param userId   {@link Long} user id
     * @param friendId {@link Long} friend id
     * @return {@link Optional} of {@link User}
     * @author Julia Seti
     */
    @Query(nativeQuery = true, value = "SELECT DISTINCT * FROM users AS u "
        + "WHERE u.id = "
        + "((SELECT user_id FROM users_friends "
        + "WHERE user_id = :userId AND friend_id = :friendId AND status = 'FRIEND') "
        + "UNION "
        + "(SELECT friend_id FROM users_friends "
        + "WHERE user_id = :friendId AND friend_id = :userId AND status = 'FRIEND'))")
    Optional<User> findUserByIdAndByFriendId(Long userId, Long friendId);

    /**
     * Retrieves the list of the user's friends (which have INPROGRESS assign to the
     * habit).
     *
     * @param habitId {@link HabitVO} id.
     * @param userId  {@link UserVO} id.
     * @return List of friends.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM ((SELECT user_id FROM users_friends AS uf "
        + "WHERE uf.friend_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.user_id "
        + "AND ha.status = 'INPROGRESS') = 1) "
        + "UNION "
        + "(SELECT friend_id FROM users_friends AS uf "
        + "WHERE uf.user_id = :userId AND uf.status = 'FRIEND' AND "
        + "(SELECT count(*) FROM habit_assign ha WHERE ha.habit_id = :habitId AND ha.user_id = uf.friend_id "
        + "AND ha.status = 'INPROGRESS') = 1)) as ui JOIN users as u ON user_id = u.id")
    List<User> getFriendsAssignedToHabit(Long userId, Long habitId);

    /**
     * Delete friend {@link User}.
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "DELETE FROM users_friends WHERE (user_id = :userId AND friend_id = :friendId)"
            + " OR (user_id = :friendId AND friend_id = :userId)")
    void deleteUserFriendById(Long userId, Long friendId);

    /**
     * Checks if a user is a friend of another user.
     *
     * @param userId   The ID of the user to check if they are a friend.
     * @param friendId The ID of the potential friend.
     * @return {@code true} if the user is a friend of the other user, {@code false}
     *         otherwise.
     */
    @Query(nativeQuery = true,
        value = "SELECT EXISTS(SELECT * FROM users_friends WHERE status = 'FRIEND' AND ("
            + "user_id = :userId AND friend_id = :friendId OR "
            + "user_id = :friendId AND friend_id = :userId))")
    boolean isFriend(Long userId, Long friendId);

    /**
     * Checks if a friend request exists between two users.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend.
     * @return {@code true} if a friend request exists between the two users,
     *         {@code false} otherwise.
     */
    @Query(nativeQuery = true,
        value = "SELECT EXISTS(SELECT * FROM users_friends WHERE status = 'REQUEST' AND ("
            + "user_id = :userId AND friend_id = :friendId OR "
            + "user_id = :friendId AND friend_id = :userId))")
    boolean isFriendRequested(Long userId, Long friendId);

    /**
     * Checks if a friend requested by current user with userId.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend.
     * @return {@code true} if a friend requested by current user, {@code false}
     *         otherwise.
     */
    @Query(nativeQuery = true,
        value = "SELECT EXISTS(SELECT * FROM users_friends WHERE status = 'REQUEST' AND "
            + "user_id = :userId AND friend_id = :friendId)")
    boolean isFriendRequestedByCurrentUser(Long userId, Long friendId);

    /**
     * Adds a new friend for a user.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend to be added.
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "INSERT INTO users_friends(user_id, friend_id, status, created_date) "
            + "VALUES (:userId, :friendId, 'REQUEST', CURRENT_TIMESTAMP) "
            + "ON CONFLICT (user_id, friend_id) DO UPDATE SET status = 'REQUEST'")
    void addNewFriend(Long userId, Long friendId);

    /**
     * Accept friend request.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend to be added.
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "UPDATE users_friends SET status = 'FRIEND' "
            + "WHERE user_id = :friendId AND friend_id = :userId")
    void acceptFriendRequest(Long userId, Long friendId);

    /**
     * Decline friend request.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend to be declined.
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "UPDATE users_friends SET  status = 'REJECTED' WHERE user_id = :friendId AND friend_id = :userId")
    void declineFriendRequest(Long userId, Long friendId);

    /**
     * Decline friend request.
     *
     * @param userId   The ID of the user who want to cancel his request.
     * @param friendId The ID of the friend to whom request was send before.
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "DELETE FROM users_friends WHERE user_id = :userId AND friend_id = :friendId")
    void canselUserRequestToFriend(Long userId, Long friendId);

    /**
     * Get all user friends.
     *
     * @param userId The ID of the user.
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN ( "
        + "(SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')"
        + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'));")
    List<User> getAllUserFriends(Long userId);

    /**
     * Get all user friends order: friends, who are tracking the same habits as user
     * with userId; friends, who live in the same city as user with userId; friends,
     * who have the highest personal rate.
     *
     * @param userId The ID of the user.
     *
     * @return list of {@link User}.
     */

    @Query(nativeQuery = true, value = ""
        + "SELECT u.* "
        + "FROM users u "
        + "LEFT JOIN user_location ul ON ul.id = u.user_location "
        + "RIGHT JOIN ("
        + " SELECT friends.id, (SELECT count(*)"
        + "FROM habit_assign "
        + " WHERE user_id = friends.id "
        + " AND habit_id in (SELECT habit_id "
        + "  FROM habit_assign ha3"
        + "  WHERE user_id = :userId AND status IN ('INPROGRESS', 'ACQUIRED', 'EXPIRED'))) count "
        + "FROM (SELECT user_id AS id FROM users_friends WHERE friend_id = :userId AND status = 'FRIEND' "
        + " UNION SELECT friend_id AS id FROM users_friends WHERE user_id = :userId AND status = 'FRIEND') friends "
        + " ) fh ON fh.id = u.id "
        + " ORDER BY fh.count desc, ul.city_en, u.rating desc ")
    Page<User> getAllUserFriendsCollectingBySpecificConditionsAndCertainOrder(Pageable pageable, Long userId);

    /**
     * Get FriendStatus related to current user with currentUserId by user with
     * userId.
     *
     * @param userId        The ID of the user.
     * @param currentUserId The ID of the current user.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT uf.status FROM users_friends as uf "
        + " WHERE friend_id = :userId and user_id = :currentUserId "
        + " OR friend_id = :currentUserId and user_id = :userId")
    String getFriendStatusByUserIdAndCurrentUserId(Long userId, Long currentUserId);

    /**
     * Get all user friends.
     *
     * @param userId   The ID of the user.
     * @param pageable current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users WHERE id IN ( "
        + "(SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')"
        + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'))")
    Page<User> getAllUserFriendsPage(Pageable pageable, Long userId);

    /**
     * Method that finds all users except current user and his friends.
     *
     * @param userId        current user's id.
     * @param filteringName name filter.
     * @param pageable      current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users u "
        + "WHERE u.id != :userId "
        + "AND u.id NOT IN ("
        + "      SELECT user_id AS id FROM users_friends WHERE friend_id = :userId AND status = 'FRIEND' "
        + "      UNION "
        + "      SELECT friend_id AS id FROM users_friends WHERE user_id = :userId AND status = 'FRIEND' "
        + ") AND LOWER(u.name) LIKE LOWER(CONCAT('%', REPLACE(REPLACE(REPLACE(REPLACE(:filteringName, '&', '\\&'), "
        + "'%', '\\%'), '_', '\\_'), '#', '\\#'), '%')) ")
    Page<User> getAllUsersExceptMainUserAndFriends(Long userId, String filteringName, Pageable pageable);

    /**
     * Method that finds all users except current user and his friends and users who
     * send request to current user.
     *
     * @param userId        current user's id.
     * @param filteringName name filter.
     * @param pageable      current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users u "
        + "WHERE u.id != :userId "
        + "AND u.id NOT IN ("
        + "      SELECT user_id AS id FROM users_friends WHERE friend_id = :userId AND status = 'FRIEND' "
        + "      UNION "
        + "      SELECT friend_id AS id FROM users_friends WHERE user_id = :userId AND status = 'FRIEND' "
        + "      UNION "
        + "      SELECT friend_id AS id FROM users_friends WHERE friend_id = :userId AND status = 'REQUEST' "
        + ") AND LOWER(u.name) LIKE LOWER(CONCAT('%', REPLACE(REPLACE(REPLACE(REPLACE(:filteringName, '&', '\\&'), "
        + "'%', '\\%'), '_', '\\_'), '#', '\\#'), '%')) ")
    Page<User> getAllUsersExceptMainUserAndFriendsAndRequestersToMainUser(Long userId, String filteringName,
        Pageable pageable);

    /**
     * Method that finds recommended friends of friends.
     *
     * @param userId   current user's id.
     * @param pageable current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT u.* FROM users  u "
        + "WHERE u.id != :userId"
        + " AND u.id IN ("
        + "    SELECT user_id FROM users_friends"
        + "        WHERE (friend_id IN (SELECT friend_id FROM users_friends WHERE user_id = :userId)"
        + "        OR friend_id IN (SELECT user_id FROM users_friends WHERE friend_id = :userId)) AND status = 'FRIEND'"
        + "      UNION"
        + "    SELECT friend_id FROM users_friends"
        + "      WHERE user_id IN (SELECT friend_id FROM users_friends WHERE user_id = :userId) AND status = 'FRIEND')")
    Page<User> getRecommendedFriendsOfFriends(Long userId, Pageable pageable);

    /**
     * Method to find users which sent request to user with userId.
     *
     * @param pageable current page.
     * @param userId   current user's id.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users u "
        + "       INNER JOIN users_friends ON u.id = users_friends.user_id "
        + "       WHERE users_friends.friend_id = :userId AND users_friends.status = 'REQUEST' ")
    Page<User> getAllUserFriendRequests(Long userId, Pageable pageable);

    /**
     * Method to find users which are friends to user with userId.
     *
     * @param userId        current user's id.
     * @param filteringName name filter.
     * @param pageable      current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users u "
        + "WHERE u.id IN ("
        + "      SELECT user_id AS id FROM users_friends WHERE friend_id = :userId AND status = 'FRIEND' "
        + "      UNION "
        + "      SELECT friend_id AS id FROM users_friends WHERE user_id = :userId AND status = 'FRIEND' "
        + ") AND LOWER(u.name) LIKE LOWER(CONCAT('%', :filteringName, '%'))")
    Page<User> findAllFriendsOfUser(Long userId, String filteringName, Pageable pageable);

    /**
     * Method to find mutual friends with friendId for current user with userId.
     *
     * @param userId   current user's id.
     * @param friendId friend id.
     * @param pageable current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users u"
        + " WHERE u.id IN ("
        + "       SELECT friend_id FROM users_friends WHERE user_id = :userId"
        + "       UNION "
        + "       SELECT user_id from users_friends WHERE friend_id = :userId)"
        + "  AND u.id IN ("
        + "       SELECT friend_id FROM users_friends  WHERE user_id = :friendId AND status = 'FRIEND'"
        + "       UNION "
        + "       SELECT user_id FROM users_friends WHERE users_friends.friend_id = :friendId AND status = 'FRIEND')")
    Page<User> getMutualFriends(Long userId, Long friendId, Pageable pageable);

    /**
     * Method that update user's rating.
     *
     * @param userId current user's id.
     * @param rating rating.
     *
     * @author Anton Bondar.
     */
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE users SET rating = :rating WHERE id = :userId")
    void updateUserRating(Long userId, Double rating);

    /**
     * Method to find recommended friends for current user by habits.
     *
     * @param userId   current user's id.
     * @param pageable current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM users u "
        + "WHERE u.id != :userId AND u.id IN("
        + "SELECT user_id FROM habit_assign WHERE status = 'ACQUIRED' OR status = 'INPROGRESS')")
    Page<User> findRecommendedFriendsByHabits(long userId, Pageable pageable);

    /**
     * Method that allow you to search users by name.
     *
     * @param searchQuery username you want to search {@link String}.
     *
     * @return list of {@link User} users.
     * @author Anton Bondar
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM users u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :searchQuery, '%'))")
    List<User> searchUsers(String searchQuery);

    /**
     * Method to find recommended friends for current user by city.
     *
     * @param userId   current user's id.
     * @param city     current user's city.
     * @param pageable current page.
     *
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT users.* FROM users "
        + "JOIN user_location ON users.user_location = user_location.id "
        + "WHERE user_location.city_ua = :city AND users.id !=:userId")
    Page<User> findRecommendedFriendsByCity(Long userId, String city, Pageable pageable);

    /**
     * Method to find user language code by userId.
     *
     * @param userId {@link Long} current user's id.
     *
     * @return {@link String}.
     * @author Olena Sotnik.
     */
    @Query(value = "SELECT l.code FROM users AS u "
        + "JOIN languages AS l "
        + "ON u.language_id = l.id "
        + "WHERE u.id = :userId", nativeQuery = true)
    String findUserLanguageCodeByUserId(Long userId);

    /**
     * Method finds friends status and requesterId.
     *
     * @param userId   {@link Long} current user's id.
     * @param friendId {@link Long} friend`s id.
     *
     * @return {@link Tuple}.
     * @author Denys Ryhal.
     */
    @Query(nativeQuery = true, value = "SELECT uf.status as status, uf.user_id as requesterId "
        + "FROM users_friends uf "
        + "WHERE (uf.user_id = :userId AND uf.friend_id = :friendId) "
        + "OR (uf.friend_id = :userId AND uf.user_id = :friendId)")
    Tuple findUsersFriendByUserIdAndFriendId(Long userId, Long friendId);

    /**
     * Method finds chatId of two users.
     *
     * @param userId   {@link Long} current user's id.
     * @param friendId {@link Long} friend`s id.
     *
     * @return {@link Long}.
     * @author Denys Ryhal.
     */
    @Query(nativeQuery = true, value = "SELECT crp.room_id FROM chat_rooms r "
        + "INNER JOIN chat_rooms_participants crp on r.id = crp.room_id "
        + "WHERE r.type = 'PRIVATE' AND crp.participant_id in (:userId,:friendId) "
        + "GROUP BY crp.room_id "
        + "HAVING COUNT(crp) = 2 LIMIT 1;")
    Long findIdOfPrivateChatOfUsers(Long userId, Long friendId);
}

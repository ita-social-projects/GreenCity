package greencity.repository;

import greencity.dto.habit.HabitVO;
import greencity.dto.user.UserLocationStatisticDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import jakarta.persistence.Tuple;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepo extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // TODO: call UserRemoteClient
    boolean existsByEmail(String email);

    /**
     * Find {@link User} by email.
     *
     * @param email user email.
     * @return {@link User}
     */
    // TODO: replace with call to UserRemoteClient
    Optional<User> findByEmail(String email);

    /**
     * Find list of {@link User}'s by emails.
     *
     * @param emails user emails.
     * @return list of {@link User}.
     */
    // TODO: replace with call to UserRemoteClient
    List<User> findAllByEmailIn(List<String> emails);

    /**
     * Find all {@link User}.
     *
     * @param filter   filter parameters
     * @param pageable pagination
     * @return list of all {@link User}
     *
     * @author Anton Bondar
     */
    @NonNull
    Page<User> findAll(@NonNull Specification<User> filter, @NonNull Pageable pageable);

    /**
     * Find id by email.
     *
     * @param email - User email
     * @return User id
     */
    // TODO
    @Query("SELECT id FROM User WHERE email=:email")
    Optional<Long> findIdByEmail(String email);

    /**
     * Updates user rating as event organizer.
     *
     * @param userId {@link User}'s id
     * @param rate   new {@link User}'s rating as event organizer
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
     */
    @Query(nativeQuery = true, value = "SELECT DISTINCT * FROM greencity_users AS u "
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
        + "AND ha.status = 'INPROGRESS') = 1)) as ui JOIN greencity_users as u ON user_id = u.id")
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
     * Retrieves the friend request status. Handles both directions.
     *
     * @param userId   The ID of the user.
     * @param friendId The ID of the friend.
     * @return the status of request.
     */
    @Query(nativeQuery = true,
        value = "SELECT status FROM users_friends WHERE (user_id = :userId AND friend_id = :friendId) "
            + "OR (user_id = :friendId AND friend_id = :userId)")
    Optional<String> getFriendRequestStatus(@Param("userId") Long userId, @Param("friendId") Long friendId);

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
        value = "UPDATE users_friends SET status = 'REJECTED' WHERE user_id = :friendId AND friend_id = :userId")
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
     * Get all user friends order: friends, who are tracking the same habits as user
     * with userId; friends, who live in the same city as user with userId; friends,
     * who have the highest personal rate.
     *
     * @param userId The ID of the user.
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = ""
        + "SELECT u.* "
        + "FROM greencity_users u "
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
     * Get all user friends.
     *
     * @param userId   The ID of the user.
     * @param pageable current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM greencity_users WHERE id IN ( "
        + "(SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')"
        + "UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'))")
    Page<User> getAllUserFriendsPage(Pageable pageable, Long userId);

    /**
     * Method that finds all users except current user and his friends.
     *
     * @param userId        current user's id.
     * @param filteringName name filter.
     * @param pageable      current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true,
        value = """
            SELECT *
            FROM greencity_users u
            WHERE u.id != :userId
              AND u.id NOT IN (
                  SELECT user_id AS id
                  FROM users_friends
                  WHERE friend_id = :userId
                    AND status = 'FRIEND'
                  UNION
                  SELECT friend_id AS id
                  FROM users_friends
                  WHERE user_id = :userId
                    AND status = 'FRIEND'
              )
              AND (
                  LOWER(u.name) LIKE LOWER(
                      CONCAT('%',
                             REPLACE(REPLACE(REPLACE(
                                    REPLACE(:filteringName, '&', '\\&'),
                                    '%', '\\%'),
                                    '_', '\\_'),
                                    '#', '\\#'), '%')
                      )
                  )
                  OR LOWER(u.user_credo) LIKE LOWER(
                      CONCAT('%',
                             REPLACE(REPLACE(REPLACE(
                                    REPLACE(:filteringName, '&', '\\&'),
                                    '%', '\\%'),
                                    '_', '\\_'),
                                    '#', '\\#'), '%')
                      )
                  OR EXISTS (
                      SELECT 1
                      FROM user_location ul
                      WHERE ul.id = u.user_location
                        AND( LOWER(ul.city_en) LIKE LOWER(
                                CONCAT('%',
                                       REPLACE(REPLACE(
                                                       REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                               '%', '\\%'),
                                                       '_', '\\_'),
                                               '#', '\\#'), '%')
                                                     )
                              OR LOWER(ul.city_uk) LIKE LOWER(
                                    CONCAT('%',
                                           REPLACE(REPLACE(
                                                           REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                                   '%', '\\%'),
                                                           '_', '\\_'),
                                                   '#', '\\#'), '%')
                                                        )
                          )
                  )
            """)

    Page<User> getAllUsersExceptMainUserAndFriends(Long userId, String filteringName, Pageable pageable);

    /**
     * Method that finds all users except current user and his friends and users who
     * send request to current user.
     *
     * @param userId        current user's id.
     * @param filteringName name filter.
     * @param pageable      current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true,
        value = """
                                SELECT *
            FROM greencity_users u
            WHERE u.id != :userId
              AND u.id NOT IN (
                SELECT user_id AS id
                FROM users_friends
                WHERE friend_id = :userId
                  AND status = 'FRIEND'
                UNION
                SELECT friend_id AS id
                FROM users_friends
                WHERE user_id = :userId
                  AND status = 'FRIEND'
            )
              AND (
                LOWER(u.name) LIKE LOWER(
                        CONCAT('%',
                               REPLACE(REPLACE(
                                               REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                       '%', '\\%'),
                                               '_', '\\_'),
                                       '#', '\\#'), '%')
                                   )
                    OR LOWER(u.user_credo) LIKE LOWER(
                        CONCAT('%',
                               REPLACE(REPLACE(
                                               REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                       '%', '\\%'),
                                               '_', '\\_'),
                                       '#', '\\#'), '%')
                                                )
                    OR EXISTS (
                    SELECT 1
                    FROM user_location ul
                    WHERE ul.id = u.user_location
                      AND( LOWER(ul.city_en) LIKE LOWER(
                              CONCAT('%',
                                     REPLACE(REPLACE(
                                                     REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                             '%', '\\%'),
                                                     '_', '\\_'),
                                             '#', '\\#'), '%')
                                                   )
                            OR LOWER(ul.city_uk) LIKE LOWER(
                                  CONCAT('%',
                                         REPLACE(REPLACE(
                                                         REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                                 '%', '\\%'),
                                                         '_', '\\_'),
                                                 '#', '\\#'), '%')
                                                      )
                        )
                )
                )
              AND (
                :filterByFriendsOfFriends = FALSE
                    OR u.id IN (
                    SELECT user_id
                    FROM users_friends
                    WHERE (friend_id IN (
                        SELECT friend_id
                        FROM users_friends
                        WHERE user_id = :userId
                    )
                        OR friend_id IN (
                            SELECT user_id
                            FROM users_friends
                            WHERE friend_id = :userId
                        ))
                      AND status = 'FRIEND'
                    UNION
                    SELECT friend_id
                    FROM users_friends
                    WHERE user_id IN (
                        SELECT friend_id
                        FROM users_friends
                        WHERE user_id = :userId
                    )
                      AND status = 'FRIEND'
                )
                )
              AND (
                :filterByCity = FALSE
                    OR EXISTS (
                    SELECT 1
                    FROM user_location ul
                    WHERE ul.id = u.user_location
                      AND ul.city_uk IN (
                        SELECT ul2.city_uk FROM user_location ul2
                                                    JOIN greencity_users u2 ON ul2.id = u2.user_location
                        WHERE u2.id = :userId
                    )
                )
                )
            """)
    Page<User> getAllUsersExceptMainUserAndFriendsAndRequestersToMainUser(Long userId,
        String filteringName,
        boolean filterByFriendsOfFriends,
        boolean filterByCity,
        Pageable pageable);

    /**
     * Method that finds recommended friends of friends.
     *
     * @param userId   current user's id.
     * @param pageable current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT u.* FROM greencity_users  u "
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
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true,
        value = """
                SELECT *
                FROM greencity_users u
                INNER JOIN users_friends
                ON u.id = users_friends.user_id
                WHERE users_friends.friend_id = :userId
                 AND users_friends.status = 'REQUEST'
                 AND (
                 LOWER(u.name) LIKE LOWER(
                         CONCAT('%',
                                REPLACE(REPLACE(
                                                REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                        '%', '\\%'),
                                                '_', '\\_'),
                                        '#', '\\#'), '%')
                                    )
                     OR LOWER(u.user_credo) LIKE LOWER(
                         CONCAT('%',
                                REPLACE(REPLACE(
                                                REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                        '%', '\\%'),
                                                '_', '\\_'),
                                        '#', '\\#'), '%')
                                                 )
                     OR EXISTS (
                     SELECT 1
                     FROM user_location ul
                     WHERE ul.id = u.user_location
                       AND( LOWER(ul.city_en) LIKE LOWER(
                           CONCAT('%',
                                  REPLACE(REPLACE(
                                                  REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                          '%', '\\%'),
                                                  '_', '\\_'),
                                          '#', '\\#'), '%')
                                                )
                         OR LOWER(ul.city_uk) LIKE LOWER(
                               CONCAT('%',
                                      REPLACE(REPLACE(
                                                      REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                              '%', '\\%'),
                                                      '_', '\\_'),
                                              '#', '\\#'), '%')
                                                   )
                     )
                 )
                 )
                              AND (
                 :filterByCity = FALSE
                     OR EXISTS (
                     SELECT 1
                     FROM user_location ul
                     WHERE ul.id = u.user_location
                       AND ul.city_uk IN (
                         SELECT ul2.city_uk FROM user_location ul2
                                                     JOIN greencity_users u2 ON ul2.id = u2.user_location
                         WHERE u2.id = :userId
                     )
                 )
                 )
            """)
    Page<User> getAllUserFriendRequests(Long userId, String filteringName, boolean filterByCity, Pageable pageable);

    /**
     * Method to find users which are friends to user with userId.
     *
     * @param userId        current user's id.
     * @param filteringName name filter.
     * @param pageable      current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true,
        value = """
                      SELECT *
               FROM greencity_users u
               WHERE u.id != :userId
                   AND u.id IN (
                       SELECT user_id AS id
                       FROM users_friends
                       WHERE friend_id = :userId
                         AND status = 'FRIEND'
                       UNION
                       SELECT friend_id AS id
                       FROM users_friends
                       WHERE user_id = :userId
                         AND status = 'FRIEND'
                   )
                   AND (
                   LOWER(u.name) LIKE LOWER(
                           CONCAT('%',
                                  REPLACE(REPLACE(
                                                  REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                          '%', '\\%'),
                                                  '_', '\\_'),
                                          '#', '\\#'), '%')
                                      )
                       OR LOWER(u.user_credo) LIKE LOWER(
                           CONCAT('%',
                                  REPLACE(REPLACE(
                                                  REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                          '%', '\\%'),
                                                  '_', '\\_'),
                                          '#', '\\#'), '%')
                                                   )
                       OR EXISTS (SELECT 1
                                  FROM user_location ul
                                  WHERE ul.id = u.user_location
                                    AND LOWER(ul.city_en) LIKE LOWER(
                                          CONCAT('%',
                                                 REPLACE(REPLACE(
                                                                 REPLACE(REPLACE(:filteringName, '&', '\\&'),
                                                                         '%', '\\%'),
                                                                 '_', '\\_'),
                                                         '#', '\\#'), '%')
                                                               ))
                   )
                   AND (
                         :filterByCity = FALSE
                             OR EXISTS (
                             SELECT 1
                             FROM user_location ul
                             WHERE ul.id = u.user_location
                               AND ul.city_uk IN (
                                 SELECT ul2.city_uk FROM user_location ul2
                                                             JOIN greencity_users u2 ON ul2.id = u2.user_location
                                 WHERE u2.id = :userId
                             )
                         )
                         )
            """)
    Page<User> findAllFriendsOfUser(Long userId, String filteringName, boolean filterByCity, Pageable pageable);

    /**
     * Method to find mutual friends with friendId for current user with userId.
     *
     * @param userId   current user's id.
     * @param friendId friend id.
     * @param pageable current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM greencity_users u"
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
     */
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE greencity_users SET rating = :rating WHERE id = :userId")
    void updateUserRating(Long userId, Double rating);

    /**
     * Method to find recommended friends for current user by habits.
     *
     * @param userId   current user's id.
     * @param pageable current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT * FROM greencity_users u "
        + "WHERE u.id != :userId AND u.id IN("
        + "SELECT user_id FROM habit_assign WHERE status = 'ACQUIRED' OR status = 'INPROGRESS')")
    Page<User> findRecommendedFriendsByHabits(long userId, Pageable pageable);

    /**
     * Method that allow you to search users by name.
     *
     * @param searchQuery username you want to search {@link String}.
     * @return list of {@link User} users.
     */
    @Query(nativeQuery = true,
        value = "SELECT * FROM greencity_users u WHERE (:searchQuery = '' OR LOWER(u.name) "
            + "LIKE LOWER(CONCAT('%', :searchQuery, '%'))) LIMIT 10")
    List<User> searchUsers(String searchQuery);

    /**
     * Method to find recommended friends for current user by city.
     *
     * @param userId   current user's id.
     * @param city     current user's city.
     * @param pageable current page.
     * @return {@link Page} of {@link User}.
     */
    @Query(nativeQuery = true, value = "SELECT greencity_users.* FROM greencity_users "
        + "JOIN user_location ON greencity_users.user_location = user_location.id "
        + "WHERE user_location.city_uk = :city AND greencity_users.id !=:userId")
    Page<User> findRecommendedFriendsByCity(Long userId, String city, Pageable pageable);

    /**
     * Method finds friends status and requesterId.
     *
     * @param userId   {@link Long} current user's id.
     * @param friendId {@link Long} friend`s id.
     * @return {@link Tuple}.
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
     * @return {@link Long}.
     */
    @Query(nativeQuery = true, value = "SELECT crp.room_id FROM chat_rooms r "
        + "INNER JOIN chat_rooms_participants crp on r.id = crp.room_id "
        + "WHERE r.type = 'PRIVATE' AND crp.participant_id in (:userId,:friendId) "
        + "GROUP BY crp.room_id "
        + "HAVING COUNT(crp) = 2 LIMIT 1;")
    Long findIdOfPrivateChatOfUsers(Long userId, Long friendId);

    /**
     * Method for getting all users who made request for joining the event.
     *
     * @param eventId  - id of the event
     * @param pageable
     *
     */
    @Query(nativeQuery = true, value = "SELECT greencity_users.* FROM greencity_users "
        + "JOIN events_requesters ON greencity_users.id = events_requesters.user_id "
        + "WHERE events_requesters.event_id = :eventId")
    Page<User> findUsersByRequestedEvents(Long eventId, Pageable pageable);

    /**
     * Retrieves the distribution of users by city.
     *
     * @param activatedUserIds A list of activated user IDs.
     * @return A list of UserLocationStatisticDto objects containing the city name
     *         and the count of users in that city.
     */
    @Query("""
        SELECT new greencity.dto.user.UserLocationStatisticDto(
               COALESCE(ul.cityEn, 'No Location'), COUNT(u.id))
        FROM User u
        LEFT JOIN u.userLocation ul
        WHERE u.id IN :activatedUserIds
        GROUP BY ul.cityEn
        """)
    List<UserLocationStatisticDto> getUserLocationsDistributionByCity(
        @Param("activatedUserIds") List<Long> activatedUserIds);

    /**
     * Retrieves the distribution of users by region.
     *
     * @param activatedUserIds A list of activated user IDs.
     * @return A list of UserLocationStatisticDto objects containing the region name
     *         and the count of users in that region.
     */
    @Query("""
        SELECT new greencity.dto.user.UserLocationStatisticDto(
               COALESCE(ul.regionEn, 'No Location'), COUNT(u.id))
        FROM User u
        LEFT JOIN u.userLocation ul
        WHERE u.id IN :activatedUserIds
        GROUP BY ul.regionEn
        """)
    List<UserLocationStatisticDto> getUserLocationsDistributionByRegion(
        @Param("activatedUserIds") List<Long> activatedUserIds);

    /**
     * Retrieves the distribution of users by country.
     *
     * @param activatedUserIds A list of activated user IDs.
     * @return A list of UserLocationStatisticDto objects containing the country
     *         name and the count of users in that country.
     */
    @Query("""
        SELECT new greencity.dto.user.UserLocationStatisticDto(
               COALESCE(ul.countryEn, 'No Location'), COUNT(u.id))
        FROM User u
        LEFT JOIN u.userLocation ul
        WHERE u.id IN :activatedUserIds
        GROUP BY ul.countryEn
        """)
    List<UserLocationStatisticDto> getUserLocationsDistributionByCountry(
        @Param("activatedUserIds") List<Long> activatedUserIds);

    /**
     * Get all user friends{@link User}.
     *
     * @return list of {@link User}.
     */
    @Query(nativeQuery = true, value = """
        SELECT id FROM greencity_users WHERE greencity_users.id IN ( \
        (SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND')\
        UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'));\
        """)
    List<Long> getAllUserFriendsIds(Long userId);

    /**
     * Get all user friends{@link User}. by page.
     *
     * @param pageable pageable configuration.
     * @return {@link Page}
     */
    @Query(nativeQuery = true, value = """
        SELECT id FROM greencity_users WHERE greencity_users.id IN ( \
        (SELECT user_id FROM users_friends WHERE friend_id = :userId and status = 'FRIEND') \
        UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId and status = 'FRIEND'))\
        """)
    Page<Long> getAllUserFriendsIds(Long userId, Pageable pageable);

    /**
     * Get six friends with the highest rating {@link User}.
     */
    @Query(nativeQuery = true, value = """
        SELECT * FROM greencity_users WHERE greencity_users.id IN ( \
        (SELECT user_id FROM users_friends WHERE friend_id = :userId AND status = 'FRIEND') \
        UNION (SELECT friend_id FROM users_friends WHERE user_id = :userId AND status = 'FRIEND')) \
        ORDER BY greencity_users.rating DESC LIMIT 6;\
        """)
    List<User> getSixFriendsWithTheHighestRating(Long userId);

    @Query(nativeQuery = true, value = """
        SELECT rating FROM greencity_users WHERE greencity_users.id = :userId
        """)
    Double findRatingById(Long userId);

    @Query(nativeQuery = true, value = "SELECT user_credo FROM greencity_users WHERE id =:userId")
    String findUserCredoByUserId(Long userId);

    @Modifying
    @Query("UPDATE User SET userCredo =:userCredo WHERE id =:userId")
    void updateUserCredo(Long userId, String userCredo);

    /**
     * Updates the profile picture path of a user by their id.
     *
     * @param userId             the id of the user
     * @param profilePicturePath the new profile picture path
     * @return the number of affected rows (1 if updated, 0 if user not found)
     */
    @Modifying
    @Query("UPDATE User u SET u.profilePicturePath =:profilePicturePath WHERE u.id =:userId")
    int updateUserProfilePictureByUserId(@Param("userId") Long userId,
        @Param("profilePicturePath") String profilePicturePath);

    /**
     * Checks if a user with the given id exists.
     *
     * @param userId the id of the user
     * @return true if a user with the given id exists, false otherwise
     */
    boolean existsById(@NotNull @Param("userId") Long userId);

    /**
     * Retrieves the profile picture path of a user by their id.
     *
     * @param userId the id of the user
     * @return the profile picture path, or null if not set
     */
    @Query("SELECT u.profilePicturePath FROM User u WHERE u.id =:userId")
    String findProfilePicturePathByUserId(@Param("userId") Long userId);
}

package greencity.repository;

import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.UserStatus;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides an interface to manage {@link User} entity.
 */
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
     * Find {@link User} by page.
     *
     * @param pageable pageable configuration.
     * @return {@link Page}
     * @author Rostyslav Khasanov
     */
    Page<User> findAll(Pageable pageable);

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
     * Find not 'DEACTIVATED' {@link User} by email.
     *
     * @param email - {@link User}'s email
     * @return found {@link User}
     * @author Vasyl Zhovnir
     */
    @Query("FROM User WHERE email=:email AND userStatus <> 1")
    Optional<User> findNotDeactivatedByEmail(String email);

    /**
     * Find all {@link User}'s with {@link EmailNotification} type.
     *
     * @param emailNotification - type of {@link EmailNotification}
     * @return list of {@link User}'s
     */
    List<User> findAllByEmailNotification(EmailNotification emailNotification);

    /**
     * Updates refresh token for a given user.
     *
     * @param refreshTokenKey - new refresh token key
     * @param id              - user's id
     * @return - number of updated rows
     * @author Yurii Koval
     */
    @Modifying
    @Query(value = "UPDATE User SET refreshTokenKey=:refreshTokenKey WHERE id=:id")
    int updateUserRefreshToken(String refreshTokenKey, Long id);

    /**
     * Counts all users by user {@link UserStatus}.
     *
     * @return amount of user with given {@link UserStatus}.
     */
    long countAllByUserStatus(UserStatus userStatus);

    /**
     * Get profile picture path {@link String}.
     *
     * @return profile picture path {@link String}
     */
    @Query("SELECT profilePicturePath FROM User WHERE id=:id")
    Optional<String> getProfilePicturePathByUserId(Long id);

    /**
     * Get all user friends{@link User}.
     *
     * @return list of {@link User}.
     */
    @Query(value = " SELECT u.userFriends FROM User u WHERE u.id = :userId ")
    List<User> getAllUserFriends(Long userId);

    /**
     * Get all user friends{@link User}. by page.
     *
     * @param pageable pageable configuration.
     * @return {@link Page}
     * @author Yurii Yhurakovskyi
     */
    @Query(value = " SELECT u.userFriends FROM User u WHERE u.id = :userId ")
    Page<User> getAllUserFriends(Long userId, Pageable pageable);

    /**
     * Delete friend {@link User}.
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "DELETE FROM users_friends WHERE user_id= :userId AND friend_id= :friendId")
    void deleteUserFriendById(Long userId, Long friendId);

    /**
     * Add new friend {@link User}.
     */
    @Modifying
    @Query(nativeQuery = true,
        value = "INSERT INTO users_friends(user_id, friend_id) VALUES (:userId, :friendId)")
    void addNewFriend(Long userId, Long friendId);

    /**
     * Get six friends with the highest rating {@link User}.
     */
    @Query(nativeQuery = true,
        value = " SELECT * FROM users_friends "
            + " LEFT JOIN users ON users.id = users_friends.friend_id "
            + " WHERE users_friends.user_id = :userId "
            + " ORDER BY users.rating DESC LIMIT 6 ")
    List<User> getSixFriendsWithTheHighestRating(Long userId);

    /**
     * Updates last activity time for a given user.
     *
     * @param userId               - {@link User}'s id
     * @param userLastActivityTime - new {@link User}'s last activity time
     * @author Yurii Zhurakovskyi
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE User SET last_activity_time=:userLastActivityTime WHERE id=:userId")
    void updateUserLastActivityTime(Long userId, Date userLastActivityTime);

    /**
     * Find the last activity time by {@link User}'s id.
     *
     * @param userId - {@link User}'s id
     * @return {@link Date}
     * @author Yurii Zhurakovskyi
     */
    @Query(nativeQuery = true,
        value = "SELECT last_activity_time FROM users WHERE id=:userId")
    Optional<Timestamp> findLastActivityTimeById(Long userId);

    /**
     * Delete from the database users that have status 'DEACTIVATED'
     * and last visited the site 2 years ago.
     *
     * @return number of deleted rows
     * @author Vasyl Zhovnir
     **/
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM users where user_status = 1 "
        + "AND last_activity_time + interval '2 year' <= CURRENT_TIMESTAMP")
    int scheduleDeleteDeactivatedUsers();

    /**
     * Set {@link User}s' statuses to 'DEACTIVATED'.
     *
     * @param ids - {@link List} of ids of {@link User} to be 'DEACTIVATED'
     * @author Vasyl Zhovnir
     **/
    @Modifying
    @Query(value = "UPDATE User SET userStatus = 1 where id IN(:ids)")
    void deactivateSelectedUsers(List<Long> ids);

    /**
     * Method returns {@link User} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link User}.
     */
    @Query("SELECT u FROM User u WHERE CONCAT(u.id,'') LIKE LOWER(CONCAT('%', :query, '%')) "
        + "OR LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%'))"
        + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')) "
        + "OR LOWER(u.userCredo) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchBy(Pageable paging, String query);
}

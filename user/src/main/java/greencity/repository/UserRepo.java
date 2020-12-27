package greencity.repository;

import greencity.dto.user.RegistrationStatisticsDtoResponse;
import greencity.dto.user.UsersFriendDto;
import greencity.entity.User;
import greencity.enums.EmailNotification;
import greencity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedNativeQuery;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * Get all user friends count.
     *
     * @param userId - {@link User}'s id
     * @return - {@link Integer} count of user friends
     */
    @Query(nativeQuery = true,
        value = " SELECT count(id) FROM users_friends "
            + " LEFT JOIN users ON users.id = users_friends.friend_id "
            + " WHERE users_friends.user_id = :userId ")
    Integer getAllUserFriendsCount(Long userId);

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
     * Delete from the database users that have status 'DEACTIVATED' and last
     * visited the site 2 years ago.
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

    /**
     * Delete from the database users that have status 'CREATED' and have not
     * activated the account within 24 hours.
     *
     * @return number of deleted rows
     * @author Vasyl Zhovnir
     **/
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM users u WHERE u.user_status = 3 "
        + "AND u.date_of_registration + interval '1 day' <= CURRENT_TIMESTAMP")
    int scheduleDeleteCreatedUsers();

    /**
     * Find and return all cities for all users.
     *
     * @return {@link List} of {@link String} of cities
     **/
    @Query("SELECT city FROM User")
    List<String> findAllUsersCities();

    /**
     * Find and return all registration months. Runs an SQL Query which is described
     * in {@link User} under {@link NamedNativeQuery} annotation. Spring Data JPA
     * can run a named native query that follows the naming convention
     * {entityClass.repositoryMethodName}.
     *
     * @return {@link List} of {@link RegistrationStatisticsDtoResponse}
     **/
    @Query(nativeQuery = true)
    List<RegistrationStatisticsDtoResponse> findAllRegistrationMonths();

    /**
     * Method that finds user's friends by given id.
     *
     * @param id {@link Long} - user's id.
     * @return {@link List} of {@link User} instances.
     */
    @Query("select u.userFriends from User u where u.id = :id")
    List<User> findUsersFriendsById(Long id);

    /**
     * Converts result of findAllRegistrationMonths() method to {@link Map}.
     *
     * @return {@link Map}
     */
    default Map<Integer, Long> findAllRegistrationMonthsMap() {
        return findAllRegistrationMonths().stream().collect(
            Collectors.toMap(RegistrationStatisticsDtoResponse::getMonth, RegistrationStatisticsDtoResponse::getCount));
    }

    /**
     * Method that finds user's recommended friends.
     *
     * @param pageable {@link Pageable}.
     * @param userId   {@link Long} -current user's id.
     * @return {@link Page} of {@link User} instances.
     */
    @Query(nativeQuery = true, value = "select * FROM public.fn_recommended_friends ( :userId )")
    Page<UsersFriendDto> findUsersRecommendedFriends(Pageable pageable,
        @Param("userId") Long userId);

    /**
     * Method that finds user.
     *
     * @param id {@link Long} -current user's id.
     * @return {@link User}.
     */
    @Query(value = "select u from User u join fetch u.userAchievements where u.id = :id")
    Optional<User> findUserForAchievement(Long id);
}

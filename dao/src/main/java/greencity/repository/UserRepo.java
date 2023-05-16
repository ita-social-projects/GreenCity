package greencity.repository;

import greencity.dto.user.UserManagementVO;
import greencity.entity.User;
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
    @Query(value = "UPDATE User SET last_activity_time=:userLastActivityTime WHERE id=:userId")
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
     * Updates user role for a given user.
     *
     * @param userId   - {@link User}'s id
     * @param userRole {@link String} - string value of user role to set
     */
    @Modifying
    @Transactional
    @Query("UPDATE User SET role = CASE "
        + "WHEN (:userRole = 'ROLE_USER') THEN 0 "
        + "WHEN (:userRole = 'ROLE_ADMIN') THEN 1 "
        + "WHEN (:userRole = 'ROLE_MODERATOR') THEN 2 "
        + "ELSE 3 END "
        + "WHERE id = :userId")
    void updateUserRole(Long userId, String userRole);

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
}
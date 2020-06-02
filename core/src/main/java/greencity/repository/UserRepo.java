package greencity.repository;

import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import greencity.entity.enums.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
     * @param id - user's id
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
     * @return list of {@link User}.
     */
    @Query(value = "SELECT * FROM public.users_friends WHERE user_id= :userId",nativeQuery = true)
    List<User> getAllUserFriends(Long userId);

    /**
     * Delete friend {@link User}.
     */
    @Query(value = "DELETE FROM public.users_friends WHERE user_id= :userId AND friends= :friendId",nativeQuery = true)
    void deleteUserFriendById(Long userId, Long friendId);

    /**
     * Add new friend {@link User}.
     * @return new Friend {@link User}.
     */
    @Query(value = "INSERT INTO public.users_friends(user_id, friends) VALUES (:userId, :friendId)", nativeQuery = true)
    void addNewFriend(Long userId, Long friendId);
}

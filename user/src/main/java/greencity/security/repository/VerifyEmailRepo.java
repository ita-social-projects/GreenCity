package greencity.security.repository;

import greencity.entity.VerifyEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link VerifyEmail}.
 *
 * @author Yurii Koval
 * @version 3.0
 */
@Repository
public interface VerifyEmailRepo extends JpaRepository<VerifyEmail, Long> {
    /**
     * Finds a record by userId and email verification token.
     *
     * @param userId - {@link Long} user's id
     * @param token  - {@link String} email verification token
     * @return - not empty {@link Optional} if a record with given userId and token
     *         exists.
     */
    @Query("SELECT v FROM VerifyEmail v WHERE v.user.id=:userId AND v.token=:token")
    Optional<VerifyEmail> findByTokenAndUserId(@Param("userId") Long userId, @Param("token") String token);

    /**
     * Deletes record for a given userId and email verification token.
     *
     * @param userId - {@link Long} user's id
     * @param token  - {@link String} email verification token
     * @return - number of modified rows.
     */
    @Modifying
    @Query("DELETE FROM VerifyEmail WHERE user.id=:userId AND token=:token")
    int deleteVerifyEmailByTokenAndUserId(@Param("userId") Long userId, @Param("token") String token);

    /**
     * Deletes from the database users that did not verify their emails on time.
     * 
     * @return number of deleted rows
     **/
    @Modifying
    @Query(
        value = "DELETE FROM User WHERE id IN "
            + "(SELECT v.user.id FROM VerifyEmail v WHERE expiryDate < CURRENT_TIMESTAMP)")
    int deleteAllUsersThatDidNotVerifyEmail();
}

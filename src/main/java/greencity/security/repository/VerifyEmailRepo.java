package greencity.security.repository;

import greencity.entity.VerifyEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for {@link VerifyEmail}.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.1
 */
@Repository
public interface VerifyEmailRepo extends JpaRepository<VerifyEmail, Long> {
    /**
     * Method that allow you find {@link VerifyEmail} by token.
     *
     * @param token - {@link String}
     * @return {@link Optional}
     */
    Optional<VerifyEmail> findByToken(String token);


    /**
     * Deletes from the database users that did not verify their emails on time.
     * @return number of deleted rows
     * @author Yurii Koval
     **/
    @Transactional
    @Modifying
    @Query(
        value = "DELETE FROM users WHERE id IN "
            + "(SELECT user_id FROM verify_emails WHERE expiry_date < CURRENT_TIMESTAMP)",
        nativeQuery = true)
    int deleteAllUsersThatDidNotVerifyEmail();
}

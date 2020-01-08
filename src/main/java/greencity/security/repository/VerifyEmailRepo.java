package greencity.security.repository;

import greencity.entity.VerifyEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link VerifyEmail}.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 2.0
 */
@Repository
public interface VerifyEmailRepo extends JpaRepository<VerifyEmail, Long> {
    /**
     * Method that allows you find {@link VerifyEmail} by token.
     *
     * @param token - {@link String}
     * @return {@link Optional}
     */
    Optional<VerifyEmail> findByToken(String token);


    /**
     * Deletes from the database users that did not verify their emails on time.
     * @return number of deleted rows
     **/
    @Modifying
    @Query(
        value = "DELETE FROM User WHERE id IN "
            + "(SELECT id FROM VerifyEmail WHERE expiryDate < CURRENT_TIMESTAMP)")
    int deleteAllUsersThatDidNotVerifyEmail();
}

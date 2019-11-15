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
 * @author Nazar Stasyuk
 * @version 1.0
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
     * Deletes from the database email verification tokens that are expired.
     *
     * @apiNote WARNING: MySQL doesn't allow to delete/update records without specifying PRIMARY KEY.
     *          `id <> -1` is a trick to make MySQL DB execute this query.
     *          However, this WON'T WORK if there is a possibility of having negative PRIMARY KEYs.
     *          Some DBMS do permit specifying IDENTITY function to generate PRIMARY KEY.
     *
     * @return number of deleted rows
     **/
    @Transactional
    @Modifying
    @Query("DELETE FROM VerifyEmail WHERE id <> -1 AND expiryDate < CURRENT_TIMESTAMP")//TODO - pass date as a parameter
    int deleteAllExpiredEmailVerificationTokens();
}

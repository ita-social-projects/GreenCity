package greencity.security.repository;

import greencity.entity.RestorePasswordEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RestorePasswordEmailRepo extends JpaRepository<RestorePasswordEmail, Long> {
    /**
     * Method that allows you find {@link RestorePasswordEmail} by token.
     *
     * @param token - {@link String}
     * @return {@link Optional}
     * @author Dmytro Dovhal
     */
    Optional<RestorePasswordEmail> findByToken(String token);

    /**
     * Deletes from the database password reset tokens that are expired.
     *
     * @apiNote WARNING: MySQL doesn't allow to delete/update records without
     *          specifying PRIMARY KEY. `id <> -1` is a trick to make MySQL DB
     *          execute this query. However, this WON'T WORK if there is a
     *          possibility of having negative PRIMARY KEYs. Some DBMS do permit
     *          specifying IDENTITY function to generate PRIMARY KEY.
     *
     * @return number of deleted rows
     * @author Yurii Koval
     **/
    @Transactional
    @Modifying
    @Query("DELETE FROM RestorePasswordEmail WHERE id <> -1 AND expiryDate < CURRENT_TIMESTAMP")
    int deleteAllExpiredPasswordResetTokens();
}

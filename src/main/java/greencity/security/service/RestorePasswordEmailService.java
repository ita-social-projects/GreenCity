package greencity.security.service;

import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides the interface to manage {@link RestorePasswordEmailService}.
 *
 * @author Dmytro Dovhal && Yurii Koval
 * @version 1.0
 */
public interface RestorePasswordEmailService {
    /**
     * Save method.
     *
     * @param user {@link User} - we use here user, who previously have been found by email.
     */
    void save(User user);

    /**
     * Method that provide delete {@link RestorePasswordEmail}.
     *
     * @param restorePasswordEmail {@link RestorePasswordEmail}
     */
    void delete(RestorePasswordEmail restorePasswordEmail);

    /**
     * Deletes from the database password reset tokens that are expired.
     * @author Yurii Koval
     */
    int deleteAllExpiredPasswordResetTokens();

    /**
     * Find all method.
     *
     * @return {@link List}
     */
    List<RestorePasswordEmail> findAll();

    /**
     * Method that check if user is not late with restoring of his email.
     *
     * @return {@code boolean}
     */
    boolean isNotExpired(LocalDateTime emailExpiredDate);
}

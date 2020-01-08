package greencity.security.service;

import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;

/**
 * Provides the interface to manage {@link RestorePasswordEmailService}.
 *
 * @author Dmytro Dovhal && Yurii Koval
 * @version 2.0
 */
public interface RestorePasswordEmailService {
    /**
     * Saves password restoration token for a given user.
     *
     * @param user {@link User}
     * @param token {@link String} - token for password restoration.
     */
    void savePasswordRestorationTokenForUser(User user, String token);

    /**
     * Method that provide delete {@link RestorePasswordEmail}.
     *
     * @param restorePasswordEmail {@link RestorePasswordEmail}
     */
    void delete(RestorePasswordEmail restorePasswordEmail);

    /**
     * Deletes from the database password reset tokens that are expired.
     */
    void deleteAllExpiredPasswordResetTokens();
}

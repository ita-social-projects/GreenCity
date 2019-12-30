package greencity.security.service;

import java.time.LocalDateTime;

/**
 * Provides the interface to manage {@link RestoreLogicService}.
 *
 * @author Dmytro Dovhal && Yurii Koval.
 * @version 2.0
 */
public interface RestoreLogicService {
    /**
     * Sends an email with password restoration token.
     *
     * @param email - {@link String}
     */
    void sendEmailForRestore(String email);

    /**
     * Restore password by token.
     *
     * @param token {@link String} - token that confirm that this user are owner of this email.
     */
    void restoreByToken(String token, String password);

    /**
     * Checks if the give date happened before.
     *
     * @param expirationDateTime - when a token expires.
     * @return {@code boolean}
     */
    boolean isNotExpired(LocalDateTime expirationDateTime);
}

package greencity.security.service;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * Provides the interface to manage {@link RestoreLogicService}.
 *
 * @author Dmytro Dovhal
 * @version 1.0
 */
public interface RestoreLogicService {
    /**
     * Method for sending email.
     *
     * @param email - {@link String}
     */
    void sendEmailForRestore(String email);

    /**
     * Deletes expiry reset tokens.
     *
     * @author Yurii Koval
     */
    @Scheduled(fixedRate = 86400000)
    void deleteExpiry();

    /**
     * Method for restore password by token.
     *
     * @param token {@link String} - token that confirm that this user are owner of this email.
     */
    void restoreByToken(String token, String password);
}

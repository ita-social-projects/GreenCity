package greencity.security.service;

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
}

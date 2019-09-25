package greencity.security.service;

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
     * Method for restore password by token.
     *
     * @param token {@link String} - token that confirm that this user are owner of this email.
     */
    void restoreByToken(String token, String password);
}

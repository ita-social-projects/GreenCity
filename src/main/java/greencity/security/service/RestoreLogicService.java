package greencity.security.service;

import greencity.entity.RestorePasswordEmail;
import greencity.entity.User;
import java.util.Optional;

public interface RestoreLogicService {
    void sendEmailForRestore(String email);

    void deleteExpiry();

    /**
     * Restore password by token.
     *
     * @param token {@link String} - token that confirm that this user are owner of this email.
     */
    void restoreByToken(String token, String password);

}

package greencity.service;

import java.time.LocalDateTime;
import java.util.List;

import greencity.entity.User;
import greencity.entity.VerifyEmail;

/**
 * Service that provide {@link VerifyEmail} logic.
 *
 * @author Nazar Stasyuk
 * @version 1.0
 */
public interface VerifyEmailService {
    /**
     * Save method.
     *
     * @param user {@link User} - we use here user, not DTO, because we create this user in sign-in
     *     and sign-up logic.
     */
    void save(User user);

    /**
     * Method that provide delete {@link VerifyEmail}
     *
     * @param verifyEmail {@link VerifyEmail}
     */
    void delete(VerifyEmail verifyEmail);

    /**
     * Verify email by token.
     *
     * @param token {@link String} - token that confirm that this user are owner of this email.
     */
    void verifyByToken(String token);

    /**
     * Find all method.
     *
     * @return {@link List<VerifyEmail>}
     */
    List<VerifyEmail> findAll();

    /**
     * Method that check if user not late with validation of his email.
     *
     * @return {@code boolean}
     */
    boolean isDateValidate(LocalDateTime emailExpiredDate);
}

package greencity.security.service;

import greencity.entity.User;
import greencity.entity.VerifyEmail;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service that provide {@link VerifyEmail} logic.
 *
 * @author Nazar Stasyuk && Yurii Koval
 * @version 1.0
 */
public interface VerifyEmailService {
    /**
     * Save method.
     *
     * @param user {@link User} - we use here user, not DTO, because we create this user in sign-in
     *             and sign-up logic.
     */
    void save(User user);

    /**
     * Method that provide delete {@link VerifyEmail}.
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
     * @return {@link List}
     */
    List<VerifyEmail> findAll();

    /**
     * Method that check if user not late with validation of his email.
     *
     * @return {@code boolean}
     */
    boolean isNotExpired(LocalDateTime emailExpiredDate);

    /**
     * Deletes email verification tokens that are expired.
     *
     * @return - number of deleted tokens
     * @author Yurii Koval
     */
    int deleteAllUsersThatDidNotVerifyEmail();
}

package greencity.security.service;

import java.time.LocalDateTime;

/**
 * Service that does email verification.
 *
 * @author Yurii Koval
 * @version 2.0
 */
public interface VerifyEmailService {
    /**
     * Verifies email by token.
     *
     * @param userId {@link Long} - user's id.
     * @param token  {@link String} - token that confirms the user is the owner of
     *               his/her email.
     */
    Boolean verifyByToken(Long userId, String token);

    /**
     * Checks whether a user is not late with email verification.
     *
     * @return {@code boolean}
     */
    boolean isNotExpired(LocalDateTime emailExpiredDate);

    /**
     * Deletes email verification tokens that are expired.
     */
    void deleteAllUsersThatDidNotVerifyEmail();
}

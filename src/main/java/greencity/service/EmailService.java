package greencity.service;

import greencity.entity.Place;
import greencity.entity.User;

/**
 * Provides the interface to manage sending emails to {@code User}.
 */
public interface EmailService {
    /**
     * Method for sending simple notification to {@code User} about change status.
     *
     * @param updatable - updated {@code Place}
     */
    void sendChangePlaceStatusEmail(Place updatable);

    /**
     * Method for sending verification email to {@link User}.
     *
     * @param user  - {@link User}
     * @param token {@link String} - email verification token
     */
    void sendVerificationEmail(User user, String token);

    /**
     * Method for sending email for restore.
     *
     * @param user  - {@link User}
     * @param token - {@link String}
     */
    void sendRestoreEmail(User user, String token);
}

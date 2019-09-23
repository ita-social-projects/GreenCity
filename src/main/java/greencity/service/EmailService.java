package greencity.service;

import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;

/**
 * Provides the interface to manage sending emails to {@code User}.
 */
public interface EmailService {
    /**
     * Method for sending simple notification to {@code User} about change status.
     *
     * @param updatable - updatable {@code Place}
     * @param status    - new {@code PlaceStatus}
     */
    void sendChangePlaceStatusEmail(Place updatable, PlaceStatus status);
}

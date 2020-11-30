package greencity.service;

import greencity.dto.category.CategoryDto;
import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.dto.place.PlaceNotificationDto;
import greencity.dto.user.PlaceAuthorDto;
import java.util.List;
import java.util.Map;

/**
 * Provides the interface to manage sending emails to {@code User}.
 */
public interface EmailService {
    /**
     * Method for sending notification to {@link User}'s who subscribed for updates
     * about added new places.
     *
     * @param subscribers          list of users for receiving email.
     * @param categoriesWithPlaces map with {@link Category} and {@link Place}`s
     *                             which were created.
     * @param notification         type of notification.
     */
    void sendAddedNewPlacesReportEmail(List<PlaceAuthorDto> subscribers,
        Map<CategoryDto, List<PlaceNotificationDto>> categoriesWithPlaces,
        String notification);

    /**
     * Method for sending news for users who subscribed for updates.
     */
    void sendNewNewsForSubscriber(List<NewsSubscriberResponseDto> subscribers,
        AddEcoNewsDtoResponse newsDto);

    /**
     * Method for sending simple notification to {@code User} about change status.
     *
     * @param authorFirstName place author's first name.
     * @param placeName       name of a place.
     * @param placeStatus     updated status of a place.
     * @param authorEmail     author's email.
     */
    void sendChangePlaceStatusEmail(String authorFirstName, String placeName,
        String placeStatus, String authorEmail);

    /**
     * Method for sending verification email to {@link User}.
     *
     * @param userId    user id.
     * @param userName  name current user.
     * @param userEmail email current user.
     * @param token     verify token current user.
     */
    void sendVerificationEmail(Long userId, String userName, String userEmail, String token, String language);

    /**
     * Method for sending user approval email to User, when Admin adds the User from
     * admin panel.
     *
     * @param userId    user id.
     * @param userName  name current user.
     * @param userEmail email current user.
     * @param token     verify token.
     */
    void sendApprovalEmail(Long userId, String userName, String userEmail, String token);

    /**
     * Sends password recovery email using separated user parameters.
     *
     * @param userId       the user id is used for recovery link building.
     * @param userFistName user first name is used in email model constants.
     * @param userEmail    user email which will be used for sending recovery
     *                     letter.
     * @param token        password recovery token.
     */
    void sendRestoreEmail(Long userId, String userFistName, String userEmail, String token, String language);

    /**
     * Sends email notification about not marked habits during 3 last days.
     *
     * @param name  user name is used in email letter.
     * @param email letter is sent to this email.
     */
    void sendHabitNotification(String name, String email);
}

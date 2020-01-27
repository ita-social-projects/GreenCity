package greencity.service;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.dto.newssubscriber.NewsSubscriberResponseDto;
import greencity.entity.Category;
import greencity.entity.Place;
import greencity.entity.User;
import greencity.entity.enums.EmailNotification;
import java.util.List;
import java.util.Map;

/**
 * Provides the interface to manage sending emails to {@code User}.
 */
public interface EmailService {
    /**
     * Method for sending notification to {@link User}'s who subscribed for updates about added new places.
     *
     * @param subscribers          - list of {@link User}'s who subscribed
     * @param categoriesWithPlaces - map of {@link Category} as a key and list of {@link Place}'s as a value
     * @param notification         - {@link EmailNotification} type
     */
    void sendAddedNewPlacesReportEmail(List<User> subscribers,
                                       Map<Category, List<Place>> categoriesWithPlaces,
                                       EmailNotification notification);

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
     * @param user  - {@link User}
     */
    void sendVerificationEmail(User user);

    /**
     * Method for sending verification email to {@link User}.
     *
     * @param userId user id.
     * @param userName name current user.
     * @param userEmail email current user.
     * @param token verify token current user.
     */
    void sendVerificationEmail(Long userId, String userName, String userEmail, String token);

    /**
     * Method for sending email for restore.
     *
     * @param user  - {@link User}
     * @param token - {@link String}
     */
    void sendRestoreEmail(User user, String token);

    /**
     * Sends password recovery email using separated user parameters.
     *
     * @param userId       the user id is used for recovery link building.
     * @param userFistName user first name is used in email model constants.
     * @param userEmail    user email which will be used for sending recovery letter.
     * @param token        password recovery token.
     */
    void sendRestoreEmail(Long userId, String userFistName, String userEmail, String token);
}

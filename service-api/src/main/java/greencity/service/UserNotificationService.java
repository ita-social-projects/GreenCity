package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.filter.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import greencity.dto.user.UserVO;
import greencity.enums.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;

@Service
public interface UserNotificationService {
    /**
     * Method to return three last new notifications.
     *
     * @param principal user to get notifications
     * @param language  language code
     * @return set of 3 last new notifications
     * @author Volodymyr Mladonov
     */
    List<NotificationDto> getThreeLastNotifications(Principal principal, String language);

    /**
     * Method for getting Notification instances filtered.
     *
     * @param pageable              page of notifications
     * @param principal             user to get notifications
     * @param filterNotificationDto contains instructions to filter notifications
     * @param language              language code
     * @return Page of {@link NotificationDto} instance.
     * @author Volodymyr Mladonov
     */
    PageableAdvancedDto<NotificationDto> getNotificationsFiltered(Pageable pageable, Principal principal,
        FilterNotificationDto filterNotificationDto, String language);

    /**
     * Method for getting page of Notification instances.
     *
     * @param pageable  page of notifications
     * @param principal user to get notifications
     * @param language  language code
     * @return Page of {@link NotificationDto} instance.
     * @author Volodymyr Mladonov
     */
    PageableAdvancedDto<NotificationDto> getNotifications(Pageable pageable, Principal principal, String language);

    /**
     * Method for getting Notification instance and marking Notification as viewed.
     *
     * @param notificationId id of requested notification
     * @param principal      user to get notifications
     * @param language       language code
     * @return {@link NotificationDto} instance.
     * @author Volodymyr Mladonov
     */
    NotificationDto getNotification(Principal principal, Long notificationId, String language);

    /**
     * Method for sending socket.
     *
     * @param user user, requested
     * @author Volodymyr Mladonov
     */
    void notificationSocket(ActionDto user);

    /**
     * Method to create Notification for many Users.
     *
     * @param attendersList    list of Users to receive Notification.
     * @param message          title of Event, {message} in template
     * @param notificationType type of Notification
     * @param targetId         represent the corresponding object's ID
     * @author Volodymyr Mladonov
     */
    void createNotificationForAttenders(List<UserVO> attendersList, String message,
        NotificationType notificationType, Long targetId);

    /**
     * Method to create Notification for many Users.
     *
     * @param attendersList    list of Users to receive Notification.
     * @param message          text to be inserted into template instead of
     *                         {message}
     * @param secondMessage    additional text, {secondMessage} in template
     * @param notificationType type of Notification
     * @param targetId         represent the corresponding object's ID
     * @author Volodymyr Mladonov
     */
    void createNotificationForAttenders(List<UserVO> attendersList, String message,
        NotificationType notificationType, Long targetId, String secondMessage);

    /**
     * Method to create Notification without CustomMessage.
     *
     * @param targetUser       user, that should receive Notification
     * @param actionUser       user, that triggered Notification
     * @param notificationType type of Notification
     * @author Volodymyr Mladonov
     */
    void createNotification(UserVO targetUser, UserVO actionUser, NotificationType notificationType);

    /**
     * Method to create Notification.
     *
     * @param targetUser       user, that should receive Notification
     * @param actionUser       user, that performed action
     * @param notificationType type of Notification
     * @param targetId         represent the corresponding object's ID
     * @param customMessage    text of Notification
     * @author Volodymyr Mladonov
     */
    void createNotification(UserVO targetUser, UserVO actionUser, NotificationType notificationType,
        Long targetId, String customMessage);

    /**
     * Method to create Notification.
     *
     * @param targetUser        user, that should receive Notification
     * @param actionUser        user, that performed action
     * @param notificationType  type of Notification
     * @param targetId          represent the corresponding object's ID
     * @param customMessage     text of Notification, {message} in template
     * @param secondMessageId   if to secondMessageText
     * @param secondMessageText additional text, {secondMessage} in template
     * @author Volodymyr Mladonov
     */
    void createNotification(UserVO targetUser, UserVO actionUser, NotificationType notificationType,
        Long targetId, String customMessage, Long secondMessageId, String secondMessageText);

    /**
     * Method to create Notification without actionUser.
     *
     * @param targetUser       user, that should receive Notification
     * @param notificationType type of Notification
     * @param targetId         represent the corresponding object's ID
     * @param customMessage    text to be inserted into Notification {message}
     * @author Volodymyr Mladonov
     */
    void createNewNotification(UserVO targetUser, NotificationType notificationType, Long targetId,
        String customMessage);

    /**
     * Method to remove ActionUser from Notification or delete Notification if that
     * was the only ActionUser. Called when user canceled the action.
     *
     * @param targetUser       User, that should receive Notification
     * @param actionUser       User, that canceled the action
     * @param targetId         represent the corresponding object's ID
     * @param notificationType type of Notification
     * @author Volodymyr Mladonov
     */
    void removeActionUserFromNotification(UserVO targetUser, UserVO actionUser, Long targetId,
        NotificationType notificationType);

    /**
     * Method to delete specific Notification.
     *
     * @param principal      user
     * @param notificationId id of notification, that should be deleted
     * @author Volodymyr Mladonov
     */
    void deleteNotification(Principal principal, Long notificationId);

    /**
     * Method to mark specific Notification as unread.
     *
     * @param notificationId id of notification, that should be marked
     */
    void unreadNotification(Long notificationId);

    /**
     * Method to mark specific Notification as read.
     *
     * @param notificationId id of notification, that should be marked
     */
    void viewNotification(Long notificationId);
}

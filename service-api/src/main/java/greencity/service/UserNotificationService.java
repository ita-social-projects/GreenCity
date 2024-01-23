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
     * @return set of 3 last new notifications
     */
    List<NotificationDto> getThreeLastNotifications(Principal principal);

    /**
     * Method for getting Notification instances filtered.
     *
     * @param pageable              page of notifications
     * @param principal             user to get notifications
     * @param filterNotificationDto contains instructions to filter notifications
     * @return Page of {@link NotificationDto} instance.
     */
    PageableAdvancedDto<NotificationDto> getNotificationsFiltered(Pageable pageable, Principal principal,
        FilterNotificationDto filterNotificationDto);

    /**
     * Method for getting page of Notification instances.
     *
     * @param pageable  page of notifications
     * @param principal user to get notifications
     * @return Page of {@link NotificationDto} instance.
     */
    PageableAdvancedDto<NotificationDto> getNotifications(Pageable pageable, Principal principal);

    /**
     * Method for getting Notification instance and marking Notification as viewed.
     *
     * @param notificationId id of requested notification
     * @param principal      user to get notifications
     * @return {@link NotificationDto} instance.
     */
    NotificationDto getNotification(Principal principal, Long notificationId);

    /**
     * Method for sending socket.
     *
     * @param user user, requested
     */
    void notificationSocket(ActionDto user);

    /**
     * Method to create Notification for many Users.
     * 
     * @param attendersList    list of Users to receive Notification.
     * @param title            title of Event
     * @param notificationType type of Notification
     * @param targetId         represent the corresponding object's ID
     */
    void createNotificationForAttenders(List<UserVO> attendersList, String title,
        NotificationType notificationType, Long targetId);

    /**
     * Method to create Notification. CustomMessage is set to actionUser name.
     * 
     * @param targetUser       user, that should receive Notification
     * @param actionUser       user, that triggered Notification
     * @param notificationType type of Notification
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
     */
    void createNotification(UserVO targetUser, UserVO actionUser, NotificationType notificationType,
        Long targetId, String customMessage);

    /**
     * Method to create Notification without actionUser.
     *
     * @param targetUser       user, that should receive Notification
     * @param notificationType type of Notification
     * @param targetId         represent the corresponding object's ID
     * @param customMessage    text to be inserted into Notification message
     */
    void createNewNotification(UserVO targetUser, NotificationType notificationType, Long targetId,
        String customMessage);

    /**
     * Method to remove ActionUser from Notification or delete Notification if that
     * was the only ActionUser. Called when user canceled the action.
     *
     * @param targetUser       User, that should receive Notification
     * @param actionUser       User, that canseled the action
     * @param targetId         represent the corresponding object's ID
     * @param notificationType type of Notification
     */
    void removeActionUserFromNotification(UserVO targetUser, UserVO actionUser, Long targetId,
        NotificationType notificationType);
}

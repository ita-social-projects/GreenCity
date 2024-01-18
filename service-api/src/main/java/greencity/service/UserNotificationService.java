package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.achievement.ActionDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.EventCommentVO;
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
     * @param pageable page of notifications
     * @param principal user to get notifications
     * @param filterNotificationDto contains instructions to filter notifications
     * @return Page of {@link NotificationDto} instance.
     */
    PageableAdvancedDto<NotificationDto> getNotificationsFiltered(Pageable pageable, Principal principal, FilterNotificationDto filterNotificationDto);

    /**
     * Method for getting page of Notification instances.
     *
     * @param pageable page of notifications
     * @param principal user to get notifications
     * @return Page of {@link NotificationDto} instance.
     */
    PageableAdvancedDto<NotificationDto> getNotifications(Pageable pageable, Principal principal);

    /**
     * Method for getting Notification instance and marking Notification as viewed.
     *
     * @param notificationId id of requested notification
     * @param principal user to get notifications
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
     * Method for creating Notification about EcoNewsComment.
     *
     * @param targetUser user, that should receive notification
     * @param actionUser user, that performed action
     * @param ecoNewsCommentId id of comment
     * @param notificationType type of notification
     */
    void createEcoNewsCommentNotification(UserVO targetUser, UserVO actionUser, Long ecoNewsCommentId,
                                          NotificationType notificationType);

    /**
     * Method for creating Notification about EcoNews for author.
     *
     * @param actionUser user, that performed action
     * @param ecoNewsId id of EcoNews
     * @param notificationType type of Notification
     */
    void createEcoNewsNotification(UserVO actionUser, Long ecoNewsId, NotificationType notificationType);

    /**
     * Method to create Notification about successful creation of EcoNews.
     *
     * @param targetUserVO user, that created EcoNews
     * @param ecoNewsVO created EcoNews
     */
    void createEcoNewsCreatedNotification(UserVO targetUserVO, EcoNewsVO ecoNewsVO);

    /**
     * Method to create Notification about successful creation of Event.
     *
     * @param targetUserVO user, that created Event
     * @param eventVO created Event
     */
    void createEventCreatedNotification(UserVO targetUserVO, EventVO eventVO);

    /**
     * Method for creating Notification about EventComment.
     *
     * @param targetUser user, that should receive notification
     * @param actionUser user, that performed action
     * @param commentId id of comment
     * @param notificationType type of notification
     */
    void createEventCommentNotification(UserVO targetUser, UserVO actionUser, Long commentId,
                                        NotificationType notificationType);

    /**
     * Method for creating Notification about Event.
     *
     * @param targetUser user, that should receive notification
     * @param actionUser user, that performed action
     * @param eventVO event
     * @param notificationType type of Notification
     */
    void createEventNotification(UserVO targetUser, UserVO actionUser, EventVO eventVO,
                                 NotificationType notificationType);

    /**
     * Method to create Notification for many Users.
     *
     * @param attendersList list of Users to receive Notification.
     * @param title title of Event
     * @param notificationType type of Notification
     * @param eventVO event of updated Event
     */
    void createEventNotificationForAttenders(List<UserVO> attendersList, String title,
                                             NotificationType notificationType, EventVO eventVO);

    /**
     * Method to create Notification
     * @param targetUser user, that should receive Notification
     * @param actionUser user, that triggered Notification
     * @param notificationType type of Notification
     */
    void createNotification(UserVO targetUser, UserVO actionUser, NotificationType notificationType);

    void removeEventCommentNotification(UserVO targetUser, UserVO actionUser, Long commentId, NotificationType notificationType);

    void deleteEventCommentNotification(Long commentId);

    void removeEcoNewsCommentNotification(UserVO targetUserVO, UserVO actionUserVO, Long commentId,
                                     NotificationType notificationType);
}

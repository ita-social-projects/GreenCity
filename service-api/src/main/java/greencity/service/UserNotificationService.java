package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.filter.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
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
}

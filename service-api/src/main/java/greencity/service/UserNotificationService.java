package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.notification.FilterNotificationDto;
import greencity.dto.notification.NotificationDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Service
public interface UserNotificationService {
    /**
     * Method to return three new notifications.
     *
     * @param principal - email of user to get notifications
     * @return set of 3 last new notifications
     */
    List<NotificationDto> getThreeLastNotifications(Principal principal);

    /**
     * Method for getting all Notification instances filtered.
     *
     * @return List of {@link NotificationDto} instance.
     */
    PageableAdvancedDto<NotificationDto> getNotifications(Pageable pageable, Principal principal, FilterNotificationDto filterNotificationDto);
}

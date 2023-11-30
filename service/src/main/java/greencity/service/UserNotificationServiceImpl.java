package greencity.service;

import greencity.dto.notification.NotificationDtoResponse;
import greencity.entity.Notification;
import greencity.repository.NotificationRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link FriendService}.
 */
@Service
@AllArgsConstructor
@Transactional
public class UserNotificationServiceImpl implements UserNotificationService {
    private final NotificationRepo notificationRepo;

    public List<NotificationDtoResponse> getThreeLastNotifications(long userId) {
        List<Notification> notifications = notificationRepo.findTop3ByTargetUserIdOrderByTimeDesc(userId);

        return null;
    }
}

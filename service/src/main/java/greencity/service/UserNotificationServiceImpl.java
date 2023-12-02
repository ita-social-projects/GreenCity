package greencity.service;

import greencity.dto.achievement.AchievementVO;
import greencity.dto.notification.NotificationDtoResponse;
import greencity.entity.Notification;
import greencity.entity.User;
import greencity.repository.NotificationRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FriendService}.
 */
@Service
@AllArgsConstructor
@Transactional
public class UserNotificationServiceImpl implements UserNotificationService {
    private final NotificationRepo notificationRepo;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public List<NotificationDtoResponse> getThreeLastNotifications(String userEmail) {
        User currentUser = modelMapper.map(userService.findByEmail(userEmail), User.class);
        Long userId = currentUser.getId();
        List<Notification> notifications = notificationRepo.findTop3ByTargetUserIdAndViewedFalseOrderByTimeDesc(userId);

        return notifications.stream()
                .map(notification -> modelMapper.map(notification, NotificationDtoResponse.class))
                .collect(Collectors.toList());
    }
}

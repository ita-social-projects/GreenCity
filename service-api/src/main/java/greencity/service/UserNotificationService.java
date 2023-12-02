package greencity.service;

import greencity.dto.notification.NotificationDtoResponse;

import java.util.List;

public interface UserNotificationService {
    List<NotificationDtoResponse> getThreeLastNotifications(String principalEmail);
    //TODO: make functions
}

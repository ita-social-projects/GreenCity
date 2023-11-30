package greencity.mapping;

import greencity.dto.notification.NotificationDtoResponse;
import greencity.dto.notification.SingleNotificationDto;
import greencity.entity.Notification;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map list of {@link Notification} into {@link NotificationDtoResponse}.
 */
@Component
public class NotificationDtoResponseMapper  extends AbstractConverter<List<Notification>, NotificationDtoResponse> {
    /**
     * Method for converting list of {@link Notification} into {@link NotificationDtoResponse}.
     *
     * @param notifications object to convert.
     * @return converted object.
     */
    @Override
    protected NotificationDtoResponse convert(List<Notification> notifications) {
        List<SingleNotificationDto> notificationDtoList = notifications.stream()
                .map(notification -> SingleNotificationDto.builder()
                        .notificationId(notification.getId())
                        .viewed(notification.isViewed())
                        .notificationType(notification.getNotificationType().name())
                        .projectName(notification.getProjectName().name())
                        .time(notification.getTime())
                        // TODO: make languages
                        .build())
                .collect(Collectors.toList());

        return NotificationDtoResponse.builder()
                .amountOfNotificationsAcquired((long) notifications.size())
                .notificationDtoList(notificationDtoList)
                .build();
    }
}

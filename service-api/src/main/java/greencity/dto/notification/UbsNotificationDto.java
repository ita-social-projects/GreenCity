package greencity.dto.notification;

import java.time.LocalDateTime;

public record UbsNotificationDto(
    Long id,
    Long orderId,
    boolean read,
    String title,
    String body,
    LocalDateTime notificationTime) {
}

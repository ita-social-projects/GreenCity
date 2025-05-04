package greencity.mapping;

import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.UbsNotificationDto;
import greencity.enums.ProjectName;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.time.ZoneOffset;
import java.util.Collections;

@Component
public class UbsNotificationDtoToNotificationDtoMapper extends AbstractConverter<UbsNotificationDto, NotificationDto> {
    @Override
    protected NotificationDto convert(UbsNotificationDto ubsNotificationDto) {
        return NotificationDto.builder()
            .actionUserId(Collections.emptyList())
            .actionUserText(Collections.emptyList())
            .bodyText(ubsNotificationDto.body())
            .message(ubsNotificationDto.body())
            .notificationId(ubsNotificationDto.id())
            .notificationType("")
            .projectName(ProjectName.PICKUP.name())
            .secondMessage("")
            .secondMessageId(0L)
            .targetId(ubsNotificationDto.orderId())
            .time(ubsNotificationDto.notificationTime().atZone(ZoneOffset.UTC))
            .titleText(ubsNotificationDto.title())
            .viewed(ubsNotificationDto.read())
            .build();
    }
}
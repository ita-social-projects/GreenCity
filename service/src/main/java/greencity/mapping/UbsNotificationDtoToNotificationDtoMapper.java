package greencity.mapping;

import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.UbsNotificationDto;
import greencity.enums.ProjectName;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

@Component
public class UbsNotificationDtoToNotificationDtoMapper extends AbstractConverter<UbsNotificationDto, NotificationDto> {

    @Override
    protected NotificationDto convert(UbsNotificationDto ubsNotificationDto) {
        return NotificationDto.builder()
                .actionUserId(Collections.emptyList())
                .actionUserText(Collections.emptyList())
                .bodyText(ubsNotificationDto.getBody())
                .message(ubsNotificationDto.getBody())
                .notificationId(ubsNotificationDto.getId())
                .notificationType("")
                .projectName(ProjectName.PICKUP.name())
                .secondMessage("")
                .secondMessageId(0L)
                .targetId(ubsNotificationDto.getOrderId())
                .time(ubsNotificationDto.getNotificationTime().atZone(ZoneOffset.UTC))
                .titleText(ubsNotificationDto.getTitle())
                .viewed(ubsNotificationDto.isRead())
                .build();
    }

}

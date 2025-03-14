package greencity.mapping;

import greencity.dto.notification.NotificationDto;
import greencity.dto.notification.UbsNotificationDto;
import greencity.enums.ProjectName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class UbsNotificationDtoToNotificationDtoMapperTest {

    @InjectMocks
    UbsNotificationDtoToNotificationDtoMapper ubsNotificationDtoToNotificationDtoMapper;

    @Test
    void convertTest() {
        String body = "body";
        long id = 1L;
        long orderId = 123L;
        LocalDateTime notificationTime = LocalDateTime.now();
        String title = "title";
        boolean isRead = false;
        UbsNotificationDto ubsNotificationDto = Mockito.mock(UbsNotificationDto.class);

        when(ubsNotificationDto.getBody())
            .thenReturn(body);
        when(ubsNotificationDto.getId())
            .thenReturn(id);
        when(ubsNotificationDto.getOrderId())
            .thenReturn(orderId);
        when(ubsNotificationDto.getNotificationTime())
            .thenReturn(notificationTime);
        when(ubsNotificationDto.getTitle())
            .thenReturn(title);
        when(ubsNotificationDto.isRead())
            .thenReturn(isRead);

        NotificationDto expected = NotificationDto.builder()
            .actionUserId(Collections.emptyList())
            .actionUserText(Collections.emptyList())
            .bodyText(body)
            .message(body)
            .notificationId(id)
            .notificationType("")
            .projectName(ProjectName.PICKUP.name())
            .secondMessage("")
            .secondMessageId(0L)
            .targetId(orderId)
            .time(notificationTime.atZone(ZoneOffset.UTC))
            .titleText(title)
            .viewed(isRead)
            .build();

        NotificationDto actual = ubsNotificationDtoToNotificationDtoMapper.convert(ubsNotificationDto);

        verify(ubsNotificationDto, times(2)).getBody();
        verify(ubsNotificationDto).getId();
        verify(ubsNotificationDto).getOrderId();
        verify(ubsNotificationDto).getNotificationTime();
        verify(ubsNotificationDto).getTitle();
        verify(ubsNotificationDto).isRead();
        assertEquals(expected, actual);
    }

}

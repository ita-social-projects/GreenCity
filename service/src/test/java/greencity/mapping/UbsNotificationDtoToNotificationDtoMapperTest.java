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

        when(ubsNotificationDto.body())
            .thenReturn(body);
        when(ubsNotificationDto.id())
            .thenReturn(id);
        when(ubsNotificationDto.orderId())
            .thenReturn(orderId);
        when(ubsNotificationDto.notificationTime())
            .thenReturn(notificationTime);
        when(ubsNotificationDto.title())
            .thenReturn(title);
        when(ubsNotificationDto.read())
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

        verify(ubsNotificationDto, times(2)).body();
        verify(ubsNotificationDto).id();
        verify(ubsNotificationDto).orderId();
        verify(ubsNotificationDto).notificationTime();
        verify(ubsNotificationDto).title();
        verify(ubsNotificationDto).read();
        assertEquals(expected, actual);
    }

}

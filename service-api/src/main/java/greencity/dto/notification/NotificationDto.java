package greencity.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class NotificationDto {
    private Long notificationId;
    private String projectName;
    private String notificationType;
    private LocalDateTime time;
    private Boolean viewed;

    private String titleText;
    private String bodyText;
    private Long actionUserId;
    private String actionUserText;
    private Long targetId;
    private String message;
    private String secondMessage;
    private Long secondMessageId;
}

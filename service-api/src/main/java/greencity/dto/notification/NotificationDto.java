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
    private Long userCount;
    private LocalDateTime time;
    private Boolean viewed;
    private Long targetId;

    //TODO: Make a language
    private String titleText;
    private String bodyText;
    private String titleTextEng;
    private String bodyTextEng;
}

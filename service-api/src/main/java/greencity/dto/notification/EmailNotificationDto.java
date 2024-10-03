package greencity.dto.notification;

import greencity.dto.user.UserVO;
import greencity.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class EmailNotificationDto {
    private UserVO targetUser;

    private List<UserVO> actionUsers = new ArrayList<>();

    private String customMessage;

    private String secondMessage;

    private NotificationType notificationType;
}

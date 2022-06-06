package greencity.dto.useraction;

import greencity.enums.UserActionType;
import lombok.*;

@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserActionMessage {
    private String userEmail;
    private UserActionType actionType;
    private Long actionId;
    private String timestamp;
}

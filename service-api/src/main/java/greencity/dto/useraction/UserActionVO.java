package greencity.dto.useraction;

import greencity.dto.user.UserVO;
import greencity.enums.ActionContextType;
import greencity.enums.UserActionType;
import lombok.*;

import java.time.ZonedDateTime;

@EqualsAndHashCode
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionVO {
    private Long id;

    private UserVO user;

    private ZonedDateTime timestamp;

    private UserActionType actionType;

    private ActionContextType contextType;

    private Long contextId;
}

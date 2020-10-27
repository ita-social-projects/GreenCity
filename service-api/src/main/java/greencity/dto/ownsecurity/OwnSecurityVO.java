package greencity.dto.ownsecurity;

import greencity.dto.user.UserVO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
public class OwnSecurityVO {
    private Long id;

    private String password;

    private UserVO user;
}

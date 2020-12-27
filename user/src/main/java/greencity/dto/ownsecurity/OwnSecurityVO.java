package greencity.dto.ownsecurity;

import greencity.dto.user.UserVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnSecurityVO {
    private Long id;

    private String password;

    private UserVO user;
}

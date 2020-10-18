package greencity.dto.user;

import greencity.enums.ROLE;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserVO {
    private Long id;

    private String name;

    private String email;

    private ROLE role;
}

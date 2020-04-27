package greencity.dto.user;

import greencity.entity.enums.ROLE;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RoleDto {
    private ROLE[] roles;
}

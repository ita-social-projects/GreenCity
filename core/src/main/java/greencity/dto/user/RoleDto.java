package greencity.dto.user;

import greencity.entity.enums.ROLE;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleDto {
    private ROLE[] roles;
}

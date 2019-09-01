package greencity.dto.user;

import greencity.entity.enums.ROLE;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRoleDto {

    @NotNull
    private Long id;

    @NotNull
    private ROLE role;
}

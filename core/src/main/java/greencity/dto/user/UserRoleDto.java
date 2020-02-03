package greencity.dto.user;

import greencity.entity.enums.ROLE;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDto {
    @NotNull
    private Long id;

    @NotNull
    private ROLE role;
}

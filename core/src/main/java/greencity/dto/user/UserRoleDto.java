package greencity.dto.user;

import greencity.entity.enums.ROLE;
import javax.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserRoleDto {
    @NotNull
    private Long id;

    @NotNull
    private ROLE role;
}

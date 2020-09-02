package greencity.dto.user;

import greencity.entity.enums.ROLE;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

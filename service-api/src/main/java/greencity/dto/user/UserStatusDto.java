package greencity.dto.user;

import greencity.enums.UserStatus;
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
public class UserStatusDto {
    @NotNull
    private Long id;

    @NotNull
    private UserStatus userStatus;
}

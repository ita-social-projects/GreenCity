package greencity.dto.user;

import greencity.enums.UserStatus;
import javax.validation.constraints.NotNull;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserStatusDto {
    @NotNull
    private Long id;

    @NotNull
    private UserStatus userStatus;
}

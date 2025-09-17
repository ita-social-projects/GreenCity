package greencity.dto.user;

import greencity.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserStatusDto {
    @NotNull
    private String email;

    @NotNull
    private UserStatus userStatus;
}

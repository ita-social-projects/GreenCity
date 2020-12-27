package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserForListDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH)
    private String name;

    private LocalDateTime dateOfRegistration;

    @Email(message = ValidationConstants.INVALID_EMAIL)
    @NotBlank
    private String email;

    @NotNull
    private UserStatus userStatus;

    @NotNull
    private Role role;

    private String userCredo;
}

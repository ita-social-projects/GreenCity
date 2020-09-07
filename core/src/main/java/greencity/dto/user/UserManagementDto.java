package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserManagementDto {
    @NotNull
    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_USERNAME)
    @Size(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH,
        message = ValidationConstants.INVALID_USERNAME_LENGTH)
    private String name;

    @Email(message = ValidationConstants.INVALID_EMAIL)
    @NotBlank(message = ValidationConstants.EMPTY_EMAIL)
    private String email;

    private String userCredo;

    @NotNull
    private ROLE role;

    @NotNull
    private UserStatus userStatus;
}

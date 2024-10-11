package greencity.dto.user;

import greencity.constant.ServiceValidationConstants;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class UserManagementUpdateDto {
    @NotBlank
    @Size(
        min = ServiceValidationConstants.USERNAME_MIN_LENGTH,
        max = ServiceValidationConstants.USERNAME_MAX_LENGTH)
    private String name;

    @Email(regexp = ServiceValidationConstants.EMAIL_REGEXP, message = ServiceValidationConstants.INVALID_EMAIL)
    @NotBlank
    private String email;

    private String userCredo;

    @NotNull
    private Role role;

    @NotNull
    private UserStatus userStatus;
}

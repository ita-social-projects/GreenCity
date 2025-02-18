package greencity.dto.user;

import greencity.annotations.SortableField;
import greencity.constant.ServiceValidationConstants;
import greencity.dto.SortableDTO;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserManagementDto implements SortableDTO {
    @NotNull
    @SortableField
    private Long id;

    @NotBlank
    @SortableField
    @Size(
        min = ServiceValidationConstants.USERNAME_MIN_LENGTH,
        max = ServiceValidationConstants.USERNAME_MAX_LENGTH)
    private String name;

    @Email(regexp = ServiceValidationConstants.EMAIL_REGEXP, message = ServiceValidationConstants.INVALID_EMAIL)
    @NotBlank
    @SortableField
    private String email;

    private String userCredo;

    @NotNull
    private Role role;

    @NotNull
    private UserStatus userStatus;
}

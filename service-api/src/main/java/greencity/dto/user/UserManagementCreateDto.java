package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserManagementCreateDto {
    @NotNull
    private Long id;

    @Pattern(
        regexp = ValidationConstants.USERNAME_REGEXP,
        message = ValidationConstants.USERNAME_MESSAGE)
    private String name;

    @Email(regexp = ValidationConstants.EMAIL_REGEXP, message = ValidationConstants.INVALID_EMAIL)
    @NotBlank
    private String email;

    @NotNull
    private Role role;
}

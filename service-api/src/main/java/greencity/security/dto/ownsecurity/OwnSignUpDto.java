package greencity.security.dto.ownsecurity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import greencity.constant.ServiceValidationConstants;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignUpDto {
    @NotBlank
    @Length(
        min = ServiceValidationConstants.USERNAME_MIN_LENGTH,
        max = ServiceValidationConstants.USERNAME_MAX_LENGTH)
    @Pattern(
        regexp = "^[a-zA-Z0-9]+([._]?[a-zA-Z0-9]+)++$",
        message = ServiceValidationConstants.INVALID_USERNAME)
    private String name;

    @NotBlank
    @Email(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
        message = ServiceValidationConstants.INVALID_EMAIL)
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]).{8,}$",
        message = ServiceValidationConstants.INVALID_PASSWORD)
    private String password;
}

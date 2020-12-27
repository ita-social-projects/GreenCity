package greencity.security.dto.ownsecurity;

import greencity.constant.ValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignUpDto {
    @NotBlank
    @Length(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH)
    @Pattern(
        regexp = "^[a-zA-Z0-9]+([._]?[a-zA-Z0-9]+)++$",
        message = ValidationConstants.INVALID_USERNAME)
    private String name;

    @NotBlank
    @Email(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
        message = ValidationConstants.INVALID_EMAIL)
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]+).{8,}$",
        message = ValidationConstants.INVALID_PASSWORD)
    private String password;
}

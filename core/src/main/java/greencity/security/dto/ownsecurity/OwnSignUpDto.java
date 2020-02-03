package greencity.security.dto.ownsecurity;

import static greencity.constant.ValidationConstants.INVALID_EMAIL;
import static greencity.constant.ValidationConstants.INVALID_PASSWORD;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignUpDto {
    @NotBlank
    @Length(max = 20)
    private String firstName;

    @NotBlank
    @Length(max = 20)
    private String lastName;

    @NotBlank
    @Email(message = INVALID_EMAIL)
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*[~`!@#$%^&*()+=_{}|:;”’?/<>,.\\]\\[]+).{8,}$",
        message = INVALID_PASSWORD
    )
    private String password;
}

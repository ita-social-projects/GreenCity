package greencity.security.dto.ownsecurity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import static greencity.constant.ValidationConstants.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignUpDto {
    @NotBlank
    @Length(
        min = USERNAME_MIN_LENGTH,
        max = USERNAME_MAX_LENGTH)
    private String name;

    @NotBlank
    @Email(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$",
            message = INVALID_EMAIL
    )
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]+).{8,}$",
        message = INVALID_PASSWORD
    )
    private String password;
}

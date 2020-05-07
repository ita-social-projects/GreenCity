package greencity.security.dto.ownsecurity;

import static greencity.constant.ValidationConstants.INVALID_EMAIL;
import static greencity.constant.ValidationConstants.INVALID_PASSWORD;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
    @Length(max = 50)
    private String name;

    @NotBlank
    @Email(message = INVALID_EMAIL)
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]+).{8,}$",
        message = INVALID_PASSWORD
    )
    private String password;

    @NotBlank
    private String lang;
}

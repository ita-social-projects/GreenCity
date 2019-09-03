package greencity.dto.userownsecurity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import static greencity.constant.ValidationConstants.INVALID_EMAIL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class UserRegisterDto {

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
    @Pattern(regexp = "^[A-z0-9~`!@#$%^&*()+=_{}|:;”’?/<>,.\\]\\[]{8,}$")
    private String password;
}

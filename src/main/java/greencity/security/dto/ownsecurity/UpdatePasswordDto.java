package greencity.security.dto.ownsecurity;

import static greencity.constant.ValidationConstants.INVALID_PASSWORD;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDto {
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*[~`!@#$%^&*()+=_{}|:;”’?/<>,.\\]\\[]+).{8,}$",
        message = INVALID_PASSWORD
    )
    private String currentPassword;
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*[~`!@#$%^&*()+=_{}|:;”’?/<>,.\\]\\[]+).{8,}$",
        message = INVALID_PASSWORD
    )
    private String password;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z]+)(?=.*[A-Z]+)(?=.*\\d+)(?=.*[~`!@#$%^&*()+=_{}|:;”’?/<>,.\\]\\[]+).{8,}$",
        message = INVALID_PASSWORD
    )
    private String confirmPassword;
}

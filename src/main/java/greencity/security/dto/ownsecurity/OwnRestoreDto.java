package greencity.security.dto.ownsecurity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnRestoreDto {
    @NotBlank
    @Pattern(regexp = "^[A-z0-9~`!@#$%^&*()+=_{}|:;”’?/<>,.\\]\\[]{8,}$")
    private String password;
    @NotBlank
    @Pattern(regexp = "^[A-z0-9~`!@#$%^&*()+=_{}|:;”’?/<>,.\\]\\[]{8,}$")
    private String confirmPassword;
    @NotBlank
    private String token;
}

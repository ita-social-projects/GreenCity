package greencity.security.dto.ownsecurity;

import greencity.constant.ServiceValidationConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordDto {
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]).{8,}$",
        message = ServiceValidationConstants.INVALID_PASSWORD)
    private String currentPassword;
    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]).{8,}$",
        message = ServiceValidationConstants.INVALID_PASSWORD)
    private String password;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~`!@#$%^&*()+=_\\-{}|:;”’?/<>,.\\]\\[]).{8,}$",
        message = ServiceValidationConstants.INVALID_PASSWORD)
    private String confirmPassword;
}

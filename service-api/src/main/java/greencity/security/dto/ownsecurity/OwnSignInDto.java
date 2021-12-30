package greencity.security.dto.ownsecurity;

import greencity.constant.ServiceValidationConstants;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OwnSignInDto {
    @NotBlank
    @Email(message = ServiceValidationConstants.INVALID_EMAIL)
    private String email;

    @NotBlank
    private String password;
}

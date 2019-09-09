package greencity.security.dto.ownsecurity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnSignInDto {
    @Email @NotBlank private String email;
    @NotBlank private String password;
}

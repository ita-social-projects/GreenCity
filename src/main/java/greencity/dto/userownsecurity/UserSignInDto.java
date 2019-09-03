package greencity.dto.user_own_security;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserSignInDto {
    @Email @NotBlank private String email;
    @NotBlank private String password;
}

package greencity.dto.user_own_security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSuccessSignInDto {
    private String email;
    private String accessToken;
    private String refreshToken;
}

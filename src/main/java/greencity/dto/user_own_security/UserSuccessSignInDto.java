package greencity.dto.user_own_security;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode

public class UserSuccessSignInDto {
    private String accessToken;
    private String refreshToken;
}

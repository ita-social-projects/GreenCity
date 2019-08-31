package greencity.dto.userownsecurity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSuccessSignInDto {
    private String accessToken;
    private String refreshToken;
}

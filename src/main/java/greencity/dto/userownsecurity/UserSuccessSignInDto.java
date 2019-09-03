package greencity.dto.userownsecurity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserSuccessSignInDto {
    private String accessToken;
    private String refreshToken;
    private String firstName;
}

package greencity.dto.user;

import java.util.List;

public record UserClaims(
        Long userId,
        String userEmail,
        List<String> roles
) {
}
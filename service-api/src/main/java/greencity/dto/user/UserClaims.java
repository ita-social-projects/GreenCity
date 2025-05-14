package greencity.dto.user;

import greencity.enums.Role;
import java.util.List;

public record UserClaims(
    Long userId,
    String userEmail,
    List<Role> roles) {
}
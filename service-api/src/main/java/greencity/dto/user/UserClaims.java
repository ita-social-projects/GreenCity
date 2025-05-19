package greencity.dto.user;

import greencity.enums.Role;
import lombok.Builder;

import java.util.List;

@Builder
public record UserClaims(
    Long userId,
    String userEmail,
    List<Role> roles) {
}
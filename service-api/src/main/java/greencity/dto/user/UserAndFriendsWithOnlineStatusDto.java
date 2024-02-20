package greencity.dto.user;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserAndFriendsWithOnlineStatusDto {
    @NotNull
    private UserWithOnlineStatusDto user;

    @NotNull
    private List<UserWithOnlineStatusDto> friends;
}

package greencity.dto.user;

import lombok.*;

import javax.validation.constraints.NotNull;
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

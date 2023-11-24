package greencity.dto.user;

import greencity.dto.PageableDto;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class UserAndAllFriendsWithOnlineStatusDto {
    @NotNull
    private UserWithOnlineStatusDto user;

    @NotNull
    private PageableDto<UserWithOnlineStatusDto> friends;
}
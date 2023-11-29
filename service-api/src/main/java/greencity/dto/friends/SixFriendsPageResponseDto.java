package greencity.dto.friends;

import greencity.dto.PageableDto;
import greencity.dto.user.UserProfilePictureDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder

public class SixFriendsPageResponseDto {
    private Integer amountOfFriends;
    private PageableDto<UserProfilePictureDto> pagedFriends;
}
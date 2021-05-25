package greencity.dto.friends;

import greencity.dto.PageableDto;
import greencity.dto.user.UserProfilePictureDto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder

public class SixFriendsPageResponceDto {
    private Integer amountOfFriends;
    private PageableDto<UserProfilePictureDto> pagedFriends;
}
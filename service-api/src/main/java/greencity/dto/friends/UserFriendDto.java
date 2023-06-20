package greencity.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserFriendDto {
    private Long id;
    private String name;
    private String city;
    private Double rating;
    private Long mutualFriends;
    private String profilePicturePath;
    private String friendStatus;
    private FriendsChatDto friendsChatDto;
}

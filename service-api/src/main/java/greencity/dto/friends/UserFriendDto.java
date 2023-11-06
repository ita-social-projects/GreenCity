package greencity.dto.friends;

import greencity.dto.location.UserLocationDto;
import greencity.enums.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@SuppressWarnings("java:S107")
public class UserFriendDto {
    private Long id;
    private String name;
    private String email;
    private Double rating;
    private Long mutualFriends;
    private String profilePicturePath;
    private Long chatId;
    private FriendStatus friendStatus;
    private UserLocationDto userLocationDto;

    /**
     * Constructor is needed for SqlResultSetMapping.
     */
    public UserFriendDto(Long id, String name, String email, Double rating, Long ulId, String cityEn,
        String cityUa, String regionEn, String regionUa, String countryEn, String countryUa,
        Double latitude, Double longitude, Long mutualFriends, String profilePicturePath, Long chatId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.rating = rating;
        this.mutualFriends = mutualFriends;
        this.profilePicturePath = profilePicturePath;
        this.chatId = chatId;
        this.userLocationDto =
            new UserLocationDto(ulId, cityEn, cityUa, regionEn, regionUa, countryEn, countryUa, latitude, longitude);
    }
}

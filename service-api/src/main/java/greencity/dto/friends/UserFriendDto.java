package greencity.dto.friends;

import greencity.annotations.Sortable;
import greencity.dto.location.UserLocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Sortable(fields = {"id", "name", "email", "rating", "mutualFriends"})
@SuperBuilder
@Data
@SuppressWarnings("java:S107")
public class UserFriendDto {
    private Long id;
    private String name;
    private String email;
    private Double rating;
    private Long mutualFriends;
    private String profilePicturePath;
    private String friendStatus;
    private Long requesterId;
    private UserLocationDto userLocationDto;

    /**
     * Constructor is needed for SqlResultSetMapping.
     */
    public UserFriendDto(Long id, String name, Double rating, Long ulId, String cityEn,
        String cityUa, String regionEn, String regionUa, String countryEn, String countryUa,
        Double latitude, Double longitude, Long mutualFriends, String profilePicturePath,
        String friendStatus, Long requesterId) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.mutualFriends = mutualFriends;
        this.profilePicturePath = profilePicturePath;
        this.friendStatus = friendStatus;
        this.requesterId = requesterId;
        if (ulId != null) {
            this.userLocationDto =
                new UserLocationDto(ulId, cityEn, cityUa, regionEn, regionUa, countryEn, countryUa, latitude,
                    longitude);
        } else {
            this.userLocationDto = null;
        }
    }
}

package greencity.dto.user;

import greencity.dto.location.UserLocationDto;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GreenCityUserProfileDtoResponse {
    private final Long userId;
    private final String email;
    private final String profilePicturePath;
    private final String userCredo;
    private final Double userRating;
    @Nullable
    private UserLocationDto userLocationDto;

    public GreenCityUserProfileDtoResponse(Long userId, String email, String profilePicturePath, String userCredo,
        Double userRating) {
        this.userId = userId;
        this.email = email;
        this.profilePicturePath = profilePicturePath;
        this.userCredo = userCredo;
        this.userRating = userRating;
    }
}

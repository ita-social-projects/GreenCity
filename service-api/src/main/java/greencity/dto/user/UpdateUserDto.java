package greencity.dto.user;

import greencity.dto.language.LanguageVO;
import greencity.dto.location.UserLocationDto;
import greencity.enums.UserUpdateType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * This class represents common fields for GreenCity and GreenCityUser user.
 * {@link UserVO} entity
 */
@Data
public class UpdateUserDto {
    private long id;
    private String name;
    @NotNull
    private String email;
    private String profilePicturePath;
    private String userCredo;
    private Double rating;
    private UserLocationDto userLocation;
    private Double eventOrganizerRating;
    private LanguageVO language;
    private UserUpdateType userUpdateType;
}

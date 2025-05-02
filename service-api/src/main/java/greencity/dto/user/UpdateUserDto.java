package greencity.dto.user;

import greencity.dto.language.LanguageVO;
import greencity.enums.UserUpdateType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * This class represents common fields for GreenCity and GreenCityUser user.
 * {@link UserVO} entity
 */
@Data
public class UpdateUserDto {
    private Long id;
    @NotNull
    private String email;
    private String name;
    private String profilePicturePath;
    private String userCredo;
    private LanguageVO language;
    private UserUpdateType userUpdateType;
}

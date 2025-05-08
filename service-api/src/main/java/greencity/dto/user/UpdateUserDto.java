package greencity.dto.user;

import greencity.enums.UserUpdateType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * This class represents common fields for GreenCity and GreenCityUser user.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto extends UserDto {
    private String userCredo;
    private UserUpdateType userUpdateType;
}

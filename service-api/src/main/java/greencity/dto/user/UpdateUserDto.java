package greencity.dto.user;

import greencity.enums.UserUpdateType;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * This class represents common fields for GreenCity and GreenCityUser user.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class UpdateUserDto extends CreateGreenCityUserDto{
    private UserUpdateType userUpdateType;
}

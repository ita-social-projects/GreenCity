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
@EqualsAndHashCode()
@SuperBuilder
public class UpdateUserDto {
    private Long id;
    private String name;
    private UserUpdateType userUpdateType;
}

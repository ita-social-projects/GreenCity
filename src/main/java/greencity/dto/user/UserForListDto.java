package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UserForListDto {

    private Long id;

    @NotBlank(message = ValidationConstants.EMPTY_FIRSTNAME)
    @Size(
            min = ValidationConstants.FIRSTNAME_MIN_LENGTH,
            max = ValidationConstants.FIRSTNAME_MAX_LENGTH,
            message = ValidationConstants.INVALID_FIRSTNAME_LENGTH)
    private String firstName;

    @NotBlank(message = ValidationConstants.EMPTY_LASTNAME)
    @Size(
            min = ValidationConstants.LASTNAME_MIN_LENGTH,
            max = ValidationConstants.LASTNAME_MAX_LENGTH,
            message = ValidationConstants.INVALID_LASTNAME_LENGTH)
    private String lastName;

    private LocalDateTime dateOfRegistration;

    @Email(message = ValidationConstants.INVALID_EMAIL)
    @NotBlank(message = ValidationConstants.EMPTY_EMAIL)
    private String email;

    @NotNull private UserStatus userStatus;

    @NotNull private ROLE role;
}

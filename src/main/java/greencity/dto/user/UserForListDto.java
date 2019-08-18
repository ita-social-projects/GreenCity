package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.entity.User;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor public class UserForListDto {

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

    public UserForListDto(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.dateOfRegistration = user.getDateOfRegistration();
        this.email = user.getEmail();
    }
}

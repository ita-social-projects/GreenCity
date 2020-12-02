package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.EmailNotification;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserUpdateDto {
    @NotBlank(message = ValidationConstants.EMPTY_USERNAME)
    @Size(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH,
        message = ValidationConstants.INVALID_USERNAME_LENGTH)
    private String name;

    @NotNull(message = ValidationConstants.EMPTY_EMAIL_NOTIFICATION)
    private EmailNotification emailNotification;
}

package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.enums.EmailNotification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
public class UserUpdateDto {
    @NotBlank
    @Size(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH)
    private String name;

    @NotNull
    private EmailNotification emailNotification;
}

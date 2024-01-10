package greencity.dto.user;

import greencity.constant.ServiceValidationConstants;
import greencity.enums.EmailNotification;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@EqualsAndHashCode
public class UserUpdateDto {
    @NotBlank
    @Size(
        min = ServiceValidationConstants.USERNAME_MIN_LENGTH,
        max = ServiceValidationConstants.USERNAME_MAX_LENGTH)
    private String name;

    @NotNull
    private EmailNotification emailNotification;
}

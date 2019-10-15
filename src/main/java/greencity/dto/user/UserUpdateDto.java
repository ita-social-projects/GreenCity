package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.EmailNotification;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserUpdateDto {
    @NotBlank
    @Length(max = 20)
    private String firstName;

    @NotBlank
    @Length(max = 20)
    private String lastName;

    @NotNull(message = ValidationConstants.EMPTY_EMAIL_NOTIFICATION)
    private EmailNotification emailNotification;
}

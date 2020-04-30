package greencity.dto.user;

import greencity.constant.ValidationConstants;
import greencity.entity.enums.EmailNotification;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@EqualsAndHashCode
public class UserUpdateDto {
    @NotBlank
    @Length(max = 50)
    private String name;

    @NotNull(message = ValidationConstants.EMPTY_EMAIL_NOTIFICATION)
    private EmailNotification emailNotification;
}

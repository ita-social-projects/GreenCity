package greencity.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import greencity.constant.ValidationConstants;
import greencity.entity.enums.ROLE;
import greencity.entity.enums.UserStatus;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserForListDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(
        min = ValidationConstants.USERNAME_MIN_LENGTH,
        max = ValidationConstants.USERNAME_MAX_LENGTH)
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateOfRegistration;

    @Email(message = ValidationConstants.INVALID_EMAIL)
    @NotBlank
    private String email;

    @NotNull
    private UserStatus userStatus;

    @NotNull
    private ROLE role;

    private String userCredo;
}

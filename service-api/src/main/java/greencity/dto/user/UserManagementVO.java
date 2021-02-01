package greencity.dto.user;

import greencity.constant.ServiceValidationConstants;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserManagementVO {
    private Long id;
    private String name;
    private String email;
    private String userCredo;
    private Role role;
    private UserStatus userStatus;
}

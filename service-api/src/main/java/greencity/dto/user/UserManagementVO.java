package greencity.dto.user;

import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@SuperBuilder
public class UserManagementVO {
    private Long id;
    private String name;
    private String email;
    private String profilePicturePath;
    private String userCredo;
    private Role role;
    private UserStatus status;
    private Double rating;
}

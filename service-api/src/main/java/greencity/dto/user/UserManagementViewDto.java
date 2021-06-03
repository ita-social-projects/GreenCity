package greencity.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserManagementViewDto {
    private String id;
    private String name;
    private String email;
    private String userCredo;
    private String role;
    private String userStatus;
}

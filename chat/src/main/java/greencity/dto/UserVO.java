package greencity.dto;

import java.time.LocalDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserVO {
    private Long id;

    private String name;

    private String email;

    /*private Role role;*/

    private String userCredo;

    /*private UserStatus userStatus;*/

    private LocalDateTime lastVisit;

    private Double rating;

    private LocalDateTime dateOfRegistration;

    private String refreshTokenKey;

    /*private OwnSecurityVO ownSecurity;*/

    private String profilePicturePath;

    private String firstName;

    private String city;

    private LocalDateTime lastActivityTime;
}

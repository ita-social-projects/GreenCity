package greencity.dto.user;

import greencity.dto.goal.CustomGoalVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.enums.EmailNotification;
import greencity.enums.ROLE;
import greencity.enums.UserStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserVO {
    private Long id;

    private String name;

    private String email;

    private ROLE role;

    private String userCredo;

    private UserStatus userStatus;

    private LocalDateTime lastVisit;

    private List<UserGoalVO> userGoals = new ArrayList<>();

    private List<CustomGoalVO> customGoals = new ArrayList<>();

    private VerifyEmailVO verifyEmail;

    private Double rating;

    private EmailNotification emailNotification;

    private LocalDateTime dateOfRegistration;

    private List<SocialNetworkVO> socialNetworks;

    private List<UserVO> userFriends = new ArrayList<>();

    private String refreshTokenKey;

    private OwnSecurityVO ownSecurity;
}

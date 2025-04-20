package greencity.dto.user;

import greencity.dto.location.UserLocationDto;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.socialnetwork.SocialNetworkVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.enums.EmailNotification;
import greencity.enums.ProfilePrivacyPolicy;
import greencity.enums.Role;
import greencity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserVO {
    private Long id;

    private String name;

    private String email;

    private Role role;

    private String userCredo;

    private UserStatus userStatus;

    private VerifyEmailVO verifyEmail;

    private Double rating;

    private EmailNotification emailNotification;

    private LocalDateTime dateOfRegistration;

    private List<SocialNetworkVO> socialNetworks;

    @Builder.Default
    private List<UserVO> userFriends = new ArrayList<>();

    private String refreshTokenKey;

    private OwnSecurityVO ownSecurity;

    private String profilePicturePath;

    private String firstName;

    private UserLocationDto userLocation;

    private ProfilePrivacyPolicy showLocation;

    private ProfilePrivacyPolicy showEcoPlace;

    private ProfilePrivacyPolicy showToDoList;

    private LocalDateTime lastActivityTime;

    private String uuid;

    private Long languageId;

    private UserLocationDto userLocationDto;
}

package greencity.dto.user;

import greencity.dto.verifyEmail.VerifyEmailVO;
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

    private VerifyEmailVO verifyEmail;

    private Double rating;
}

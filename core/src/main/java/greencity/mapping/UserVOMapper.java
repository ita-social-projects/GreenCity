package greencity.mapping;

import greencity.dto.goal.CustomGoalVO;
import greencity.dto.goal.GoalVO;
import greencity.dto.ownsecurity.OwnSecurityVO;
import greencity.dto.user.UserGoalVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyemail.VerifyEmailVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserVOMapper extends AbstractConverter<User, UserVO> {
    @Override
    protected UserVO convert(User user) {
        VerifyEmailVO verifyEmailVO = new VerifyEmailVO();
        if(user.getVerifyEmail()!= null) {
            verifyEmailVO = VerifyEmailVO.builder()
                    .id(user.getVerifyEmail().getId())
                    .user(UserVO.builder()
                            .id(user.getVerifyEmail().getUser().getId())
                            .name(user.getVerifyEmail().getUser().getName())
                            .build())
                    .expiryDate(user.getVerifyEmail().getExpiryDate())
                    .token(user.getVerifyEmail().getToken())
                    .build();
        }
        List<UserGoalVO> userGoalVOs = new ArrayList<>();
        if(user.getUserGoals()!= null) {
            userGoalVOs = user.getUserGoals()
                    .stream().map(userGoal -> UserGoalVO.builder()
                            .id(userGoal.getId())
                            .dateCompleted(userGoal.getDateCompleted())
                            .status(userGoal.getStatus())
                            .goal(GoalVO.builder()
                                    .id(userGoal.getGoal().getId())
                                    .build())
                            .user(UserVO.builder()
                                    .id(userGoal.getUser().getId())
                                    .build())
                            .customGoal(CustomGoalVO.builder()
                                    .id(userGoal.getCustomGoal().getId())
                                    .build())
                            .build())
                    .collect(Collectors.toList());
        }
        List<CustomGoalVO> customGoalVOS = new ArrayList<>();
        if(user.getCustomGoals()!= null) {
            customGoalVOS = user.getCustomGoals()
                    .stream().map(customGoal -> CustomGoalVO.builder()
                            .id(customGoal.getId())
                            .text(customGoal.getText())
                            .build()).collect(Collectors.toList());
        }
        List<UserVO> userFriends = new ArrayList<>();
        if(user.getUserGoals()!= null) {
            userFriends = user.getUserFriends()
                    .stream().map(user1 -> UserVO.builder()
                            .id(user1.getId())
                            .name(user1.getName())
                            .build())
                    .collect(Collectors.toList());
        }
        OwnSecurityVO ownSecurityVO = new OwnSecurityVO();
        if(user.getOwnSecurity()!= null) {
            ownSecurityVO = OwnSecurityVO.builder()
                    .id(user.getOwnSecurity().getId())
                    .password(user.getOwnSecurity().getPassword())
                    .user(UserVO.builder()
                            .id(user.getOwnSecurity().getUser().getId())
                            .build())
                    .build();
        }
        return UserVO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .userCredo(user.getUserCredo())
                .userStatus(user.getUserStatus())
                .lastVisit(user.getLastVisit())
                .rating(user.getRating())
                .verifyEmail(user.getVerifyEmail() == null ? null : verifyEmailVO)
                .userGoals(userGoalVOs)
                .customGoals(customGoalVOS)
                .userFriends(userFriends)
                .refreshTokenKey(user.getRefreshTokenKey())
                .ownSecurity(ownSecurityVO)
                .dateOfRegistration(user.getDateOfRegistration())
                .build();
    }
}

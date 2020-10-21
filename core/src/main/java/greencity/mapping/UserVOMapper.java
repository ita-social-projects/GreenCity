package greencity.mapping;

import greencity.dto.goal.CustomGoalVO;
import greencity.dto.goal.GoalVO;
import greencity.dto.user.UserGoalVO;
import greencity.dto.user.UserVO;
import greencity.dto.verifyEmail.VerifyEmailVO;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserVOMapper extends AbstractConverter<User, UserVO> {
    @Override
    protected UserVO convert(User user) {
        return UserVO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .userCredo(user.getUserCredo())
                .userStatus(user.getUserStatus())
                .lastVisit(user.getLastVisit())
                .rating(user.getRating())
                .verifyEmail(VerifyEmailVO.builder()
                        .id(user.getVerifyEmail().getId())
                        .expiryDate(user.getVerifyEmail().getExpiryDate())
                        .token(user.getVerifyEmail().getToken())
                        .build())
                .userGoals(user.getUserGoals()
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
                        .collect(Collectors.toList()))
                .build();
    }
}

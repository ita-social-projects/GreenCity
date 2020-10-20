package greencity.mapping;

import greencity.dto.user.UserGoalVO;
import greencity.entity.CustomGoal;
import greencity.entity.Goal;
import greencity.entity.User;
import greencity.entity.UserGoal;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserGoalMapper extends AbstractConverter<UserGoalVO, UserGoal> {
    @Override
    protected UserGoal convert(UserGoalVO userGoalVO) {
        return UserGoal.builder()
            .id(userGoalVO.getId())
            .status(userGoalVO.getStatus())
            .customGoal(CustomGoal.builder()
                .id(userGoalVO.getCustomGoal().getId())
                .text(userGoalVO.getCustomGoal().getText())
                .build())
            .user(User.builder()
                .id(userGoalVO.getUser().getId())
                .email(userGoalVO.getUser().getEmail())
                .name(userGoalVO.getUser().getName())
                .role(userGoalVO.getUser().getRole())
                .build())
            .goal(Goal.builder()
                .id(userGoalVO.getGoal().getId())
                .build())
            .dateCompleted(userGoalVO.getDateCompleted())
            .build();
    }
}

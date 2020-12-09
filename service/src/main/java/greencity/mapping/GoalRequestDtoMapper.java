package greencity.mapping;

import greencity.dto.goal.GoalRequestDto;
import greencity.entity.Goal;
import greencity.entity.UserGoal;
import greencity.enums.GoalStatus;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class GoalRequestDtoMapper extends AbstractConverter<GoalRequestDto, UserGoal> {
    /**
     * Method for converting {@link GoalRequestDto} into {@link UserGoal}.
     *
     * @param goalRequestDto object to convert.
     * @return converted object.
     */
    @Override
    protected UserGoal convert(GoalRequestDto goalRequestDto) {
        return UserGoal.builder()
            .goal(Goal.builder().id(goalRequestDto.getId()).build())
            .status(GoalStatus.ACTIVE)
            .build();
    }
}

package greencity.mapping;

import greencity.dto.user.UserGoalResponseDto;
import greencity.entity.UserGoal;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class UserGoalResponseDtoMapper extends AbstractConverter<UserGoal, UserGoalResponseDto> {
    /**
     * Method for converting {@link UserGoal} into {@link UserGoalResponseDto}.
     *
     * @param userGoal object to convert.
     * @return converted object.
     */
    @Override
    public UserGoalResponseDto convert(UserGoal userGoal) {
        return UserGoalResponseDto.builder()
            .id(userGoal.getId())
            .status(userGoal.getStatus())
            .build();
    }
}
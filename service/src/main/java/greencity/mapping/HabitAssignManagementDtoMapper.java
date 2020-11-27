package greencity.mapping;

import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.HabitAssign;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitAssign} into
 * {@link HabitAssignManagementDto}.
 */
@Component
public class HabitAssignManagementDtoMapper extends
    AbstractConverter<HabitAssign, HabitAssignManagementDto> {
    /**
     * Method convert {@link HabitAssign} to {@link HabitAssignManagementDto}.
     *
     * @return {@link HabitAssignManagementDto}
     */
    @Override
    protected HabitAssignManagementDto convert(HabitAssign habitAssign) {
        return HabitAssignManagementDto.builder()
            .id(habitAssign.getId())
            .status(habitAssign.getStatus())
            .createDateTime(habitAssign.getCreateDate())
            .userId(habitAssign.getUser().getId())
            .habitId(habitAssign.getHabit().getId())
            .duration(habitAssign.getDuration())
            .habitStreak(habitAssign.getHabitStreak())
            .workingDays(habitAssign.getWorkingDays())
            .lastEnrollment(habitAssign.getLastEnrollmentDate())
            .build();
    }
}
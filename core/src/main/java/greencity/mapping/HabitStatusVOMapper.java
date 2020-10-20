package greencity.mapping;

import greencity.dto.habit.HabitAssignVO;
import greencity.dto.habitstatus.HabitStatusVO;
import greencity.entity.HabitStatus;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link HabitStatus} into
 * {@link HabitStatusVO}.
 */
@Component
public class HabitStatusVOMapper extends AbstractConverter<HabitStatus, HabitStatusVO> {
    /**
     * Method convert {@link HabitStatus} to {@link HabitStatusVO}.
     *
     * @return {@link HabitStatusVO}
     */
    @Override
    protected HabitStatusVO convert(HabitStatus habitStatus) {
        return HabitStatusVO.builder()
            .id(habitStatus.getId())
            .habitStreak(habitStatus.getHabitStreak())
            .lastEnrollmentDate(habitStatus.getLastEnrollmentDate())
            .workingDays(habitStatus.getWorkingDays())
            .habitAssignVO(HabitAssignVO.builder()
                .id(habitStatus.getHabitAssign().getId()).build())
            .build();
    }
}

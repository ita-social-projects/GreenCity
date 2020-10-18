package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.entity.HabitStatus;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class HabitStatusDtoMapperTest {
    @InjectMocks
    private HabitStatusDtoMapper habitStatusDtoMapper;

    @Test
    void convert() {
        HabitStatus habitStatus = ModelUtils.getHabitStatus();

        HabitAssignDto habitAssignDto = HabitAssignDto.builder()
                .id(habitStatus.getHabitAssign().getId())
                .acquired(habitStatus.getHabitAssign().getAcquired())
                .suspended(habitStatus.getHabitAssign().getSuspended())
                .createDateTime(habitStatus.getHabitAssign().getCreateDate())
                .habit(HabitDto.builder().id(habitStatus.getHabitAssign().getHabit().getId()).build()).build();

        HabitStatusDto expected = new HabitStatusDto(habitStatus.getId(),
                habitStatus.getWorkingDays(), habitStatus.getHabitStreak(),
                habitStatus.getLastEnrollmentDate(), habitAssignDto);

        HabitStatusDto actual = habitStatusDtoMapper.convert(habitStatus);

        assertEquals(expected, actual);
    }
}
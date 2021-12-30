package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habit.HabitAssignManagementDto;
import greencity.entity.HabitAssign;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class HabitAssignManagementDtoMapperTest {
    @InjectMocks
    HabitAssignManagementDtoMapper habitAssignManagementDtoMapper;

    @Test
    void convert() {
        HabitAssign habitAssign = ModelUtils.getHabitAssign();
        HabitAssignManagementDto expected = HabitAssignManagementDto.builder()
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
        HabitAssignManagementDto actual = habitAssignManagementDtoMapper.convert(habitAssign);
        assertEquals(expected, actual);
    }
}
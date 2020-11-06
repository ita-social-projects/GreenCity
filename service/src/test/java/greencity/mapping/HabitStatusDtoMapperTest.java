package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.habitstatus.HabitStatusDto;
import greencity.dto.habitstatuscalendar.HabitStatusCalendarDto;
import greencity.entity.HabitStatus;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        HabitStatusDto expected = HabitStatusDto.builder()
            .id(habitStatus.getId())
            .habitStreak(habitStatus.getHabitStreak())
            .lastEnrollmentDate(habitStatus.getLastEnrollmentDate())
            .workingDays(habitStatus.getWorkingDays())
            .habitAssignId(habitStatus.getHabitAssign().getId())
            .habitStatusCalendarDtos(habitStatus.getHabitStatusCalendars().stream()
                .map(habitStatusCalendar -> HabitStatusCalendarDto.builder()
                    .id(habitStatusCalendar.getId())
                    .enrollDate(habitStatusCalendar.getEnrollDate())
                    .build())
                .collect(Collectors.toList()))
            .build();

        HabitStatusDto actual = habitStatusDtoMapper.convert(habitStatus);

        assertEquals(expected, actual);
    }
}
package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.HabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.exception.NotFoundException;
import greencity.repository.HabitRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class HabitStatisticMapper implements MapperToDto<HabitStatistic, HabitStatisticDto>,
    MapperToEntity<HabitStatisticDto, HabitStatistic> {
    private ModelMapper modelMapper;
    private HabitRepo habitRepo;

    @Override
    public HabitStatistic convertToEntity(HabitStatisticDto dto) {
        Habit habit = habitRepo.findById(dto.getHabitId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + dto.getHabitId()));

        HabitStatistic habitStatistic = modelMapper.map(dto, HabitStatistic.class);
        habitStatistic.setHabit(habit);
        habitStatistic.setCreatedOn(dto.getDate());
        return habitStatistic;
    }

    @Override
    public HabitStatisticDto convertToDto(HabitStatistic entity) {
        HabitStatisticDto habitStatisticDto = modelMapper.map(entity, HabitStatisticDto.class);
        habitStatisticDto.setHabitId(entity.getHabit().getId());
        habitStatisticDto.setDate(entity.getCreatedOn());
        return habitStatisticDto;
    }
}

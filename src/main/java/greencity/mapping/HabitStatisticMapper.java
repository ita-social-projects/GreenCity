package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.AddHabitStatisticDto;
import greencity.entity.Habit;
import greencity.entity.HabitStatistic;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class HabitStatisticMapper implements MapperToDto<HabitStatistic, AddHabitStatisticDto>,
    MapperToEntity<AddHabitStatisticDto, HabitStatistic> {
    private ModelMapper modelMapper;
    private HabitRepo habitRepo;

    @Override
    public HabitStatistic convertToEntity(AddHabitStatisticDto dto) {
        Habit habit = habitRepo.findById(dto.getHabitId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + dto.getHabitId()));

        HabitStatistic habitStatistic = modelMapper.map(dto, HabitStatistic.class);
        habitStatistic.setHabit(habit);
        return habitStatistic;
    }

    @Override
    public AddHabitStatisticDto convertToDto(HabitStatistic entity) {
        AddHabitStatisticDto addHabitStatisticDto = modelMapper.map(entity, AddHabitStatisticDto.class);
        addHabitStatisticDto.setHabitId(entity.getHabit().getId());
        return addHabitStatisticDto;
    }
}

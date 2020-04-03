package greencity.mapping;

import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitDictionaryDto;
import greencity.entity.Habit;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

/**
 * Class that used by {@link ModelMapper} to map {@link Habit} into
 * {@link HabitCreateDto}.
 */
public class HabitCreateDtoMapper extends AbstractConverter<Habit, HabitCreateDto> {
    /**
     * Method convert {@link Habit} to {@link HabitCreateDto}.
     *
     * @return {@link HabitCreateDto}
     */
    @Override
    protected HabitCreateDto convert(Habit habit) {
        HabitCreateDto habitCreateDto = new HabitCreateDto();
        HabitDictionaryDto habitDictionaryDto = new HabitDictionaryDto();
        habitDictionaryDto.setImage(habit.getHabitDictionary().getImage());
        habitDictionaryDto.setId(habit.getHabitDictionary().getId());
        habitCreateDto.setId(habit.getId());
        habitCreateDto.setStatus(habit.getStatusHabit());
        habitCreateDto.setHabitDictionary(habitDictionaryDto);
        return habitCreateDto;
    }
}

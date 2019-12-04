package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.entity.Habit;
import greencity.entity.HabitDictionary;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitDictionaryRepo;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class HabitMapper implements MapperToDto<Habit, HabitCreateDto> {
    /**
     * Autowired.
     */
    private HabitDictionaryRepo habitDictionaryRepo;
    private HabitDictionaryMapper habitDictionaryMapper;


    @Override
    public HabitCreateDto convertToDto(Habit entity) {
        HabitCreateDto habitCreateDto = new HabitCreateDto();
        habitCreateDto.setId(entity.getId());
        habitCreateDto.setStatus(entity.getStatusHabit());
        habitCreateDto.setHabitDictionary(habitDictionaryMapper.convertToDto(entity.getHabitDictionary()));
        return habitCreateDto;
    }

    /**
     * Convert to habit entity.
     * @param id {@link HabitDictionary}
     * @param user {@link User} current user.
     * @return {@link Habit}
     */
    public Habit convertToEntity(Long id, User user) {
        HabitDictionary dictionary = habitDictionaryRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
        Habit habit = new Habit();
        habit.setUser(user);
        habit.setCreateDate(LocalDate.now());
        habit.setHabitDictionary(dictionary);
        habit.setStatusHabit(true);
        return habit;
    }
}

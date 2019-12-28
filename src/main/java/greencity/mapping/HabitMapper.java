package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.entity.Habit;
import greencity.entity.HabitDictionary;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitDictionaryRepo;
import greencity.converters.DateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HabitMapper implements MapperToDto<Habit, HabitCreateDto> {
    private final HabitDictionaryRepo habitDictionaryRepo;
    private final HabitDictionaryMapper habitDictionaryMapper;
    private final DateService dateService;

    /**
     * Constructor.
     *
     * @param habitDictionaryRepo {@link HabitDictionaryRepo}
     * @param habitDictionaryMapper {@link HabitDictionaryMapper}
     * @param dateService {@link DateService}
     */
    @Autowired
    public HabitMapper(HabitDictionaryRepo habitDictionaryRepo,
                       HabitDictionaryMapper habitDictionaryMapper,
                       DateService dateService) {
        this.habitDictionaryRepo = habitDictionaryRepo;
        this.habitDictionaryMapper = habitDictionaryMapper;
        this.dateService = dateService;
    }


    /**
     * Converts {@link Habit} entity to {@link HabitCreateDto}.
     *
     * @param entity to map from.
     * @return {@link HabitCreateDto}
     */
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
        habit.setCreateDate(dateService.getDatasourceZonedDateTime());
        habit.setHabitDictionary(dictionary);
        habit.setStatusHabit(true);
        return habit;
    }
}

package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.dto.habitstatistic.HabitDictionaryDto;
import greencity.entity.Habit;
import greencity.entity.HabitDictionary;
import greencity.entity.HabitDictionaryTranslation;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitDictionaryRepo;
import greencity.repository.HabitDictionaryTranslationRepo;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class HabitMapper {
    /**
     * Autowired.
     */
    private HabitDictionaryRepo habitDictionaryRepo;
    private HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;

    /**
     * Method convert Entity ti Dto.
     *
     * @param entity   {@link Habit}.
     * @param language language code
     * @return {@link HabitCreateDto}
     */
    public HabitCreateDto convertToDto(Habit entity, String language) {
        HabitDictionaryTranslation htd = habitDictionaryTranslationRepo
            .findByHabitDictionaryAndLanguageCode(entity.getHabitDictionary(), language)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_DACTIONARY_TRANSLATION_NOT_FOUnD));
        final HabitCreateDto habitCreateDto = new HabitCreateDto();
        final HabitDictionaryDto habitDictionaryDto = new HabitDictionaryDto();
        habitDictionaryDto.setName(htd.getName());
        habitDictionaryDto.setDescription(htd.getDescription());
        habitDictionaryDto.setImage(entity.getHabitDictionary().getImage());
        habitDictionaryDto.setId(entity.getHabitDictionary().getId());
        habitCreateDto.setId(entity.getId());
        habitCreateDto.setStatus(entity.getStatusHabit());
        habitCreateDto.setHabitDictionary(habitDictionaryDto);
        return habitCreateDto;
    }

    /**
     * Convert to habit entity.
     *
     * @param id   {@link HabitDictionary}
     * @param user {@link User} current user.
     * @return {@link Habit}
     */
    public Habit convertToEntity(Long id, User user) {
        HabitDictionary dictionary = habitDictionaryRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
        Habit habit = new Habit();
        habit.setUser(user);
        habit.setCreateDate(ZonedDateTime.now());
        habit.setHabitDictionary(dictionary);
        habit.setStatusHabit(true);
        return habit;
    }
}

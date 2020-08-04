package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.entity.Habit;
import greencity.entity.HabitDictionaryTranslation;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitDictionaryTranslationRepo;
import greencity.repository.HabitRepo;
import greencity.service.HabitService;
import greencity.service.HabitStatusService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link HabitService}.
 *
 * @author Kovaliv Taras
 */
@Service
@AllArgsConstructor
public class HabitServiceImpl implements HabitService {
    private HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;
    private HabitRepo habitRepo;
    private HabitStatusService habitStatusService;
    private final ModelMapper modelMapper;

    /**
     * Method {@link HabitDictionaryTranslation} by {@link Habit} and languageCode.
     *
     * @return {@link HabitDictionaryTranslation}
     * @author Kovaliv Taras
     */
    @Override
    public HabitDictionaryTranslation getHabitDictionaryTranslation(Habit habit, String languageCode) {
        return habitDictionaryTranslationRepo
            .findByHabitDictionaryAndLanguageCode(habit.getHabitDictionary(), languageCode)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.HABIT_DICTIONARY_TRANSLATION_NOT_FOUND));
    }

    /**
     * Method find {@link Habit} by id.
     *
     * @param id - id of Habit
     * @return {@link Habit}
     * @author Kovaliv Taras
     */
    @Override
    public Habit getById(Long id) {
        return habitRepo.findById(id)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + id));
    }

    /**
     * Method assign {@link Habit} for user
     * @param habitId - id of habit user want to assign
     * @param user - user that assign habit
     * @return {@link HabitCreateDto}
     */
    @Transactional
    @Override
    public HabitCreateDto assignHabitForUser(Long habitId, User user) {
        Habit habit = habitRepo.findById(habitId)
            .orElseThrow(() -> new WrongIdException(ErrorMessage.HABIT_NOT_FOUND_BY_ID + habitId));

        if (!habit.getUsers().contains(user)) {
            habit.getUsers().add(user);
            habitStatusService.saveByHabit(habit, user);
        } else {
            habitStatusService.deleteByUser(user.getId());
            habit.getUsers().remove(user);
            habitRepo.save(habit);
        }

        return modelMapper.map(habitRepo.save(habit), HabitCreateDto.class);
    }
}
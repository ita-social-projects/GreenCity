package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.Habit;
import greencity.entity.HabitDictionaryTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.WrongIdException;
import greencity.repository.HabitDictionaryTranslationRepo;
import greencity.repository.HabitRepo;
import greencity.service.HabitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
}

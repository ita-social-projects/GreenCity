package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.Habit;
import greencity.entity.HabitDictionaryTranslation;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.HabitDictionaryTranslationRepo;
import greencity.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HabitService}.
 *
 * @author Kovaliv Taras
 */
@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {
    private HabitDictionaryTranslationRepo habitDictionaryTranslationRepo;

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
}

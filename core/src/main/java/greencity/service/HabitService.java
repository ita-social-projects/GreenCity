package greencity.service;

import greencity.entity.Habit;
import greencity.entity.HabitDictionaryTranslation;

public interface HabitService {
    /**
     * Method find {@link HabitDictionaryTranslation} by {@link Habit} and languageCode.
     *
     * @return {@link HabitDictionaryTranslation}
     * @author Kovaliv Taras
     */
    HabitDictionaryTranslation getHabitDictionaryTranslation(Habit habit, String languageCode);

    /**
     * Method find {@link Habit} by id.
     *
     * @return {@link Habit}
     * @author Kovaliv Taras
     */
    Habit getById(Long id);
}

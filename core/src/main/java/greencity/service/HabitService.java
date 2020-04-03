package greencity.service;

import greencity.entity.Habit;
import greencity.entity.HabitDictionaryTranslation;

public interface HabitService {
    /**
     * Method {@link HabitDictionaryTranslation} by {@link Habit} and languageCode.
     *
     * @return {@link HabitDictionaryTranslation}
     * @author Kovaliv Taras
     */
    HabitDictionaryTranslation getHabitDictionaryTranslation(Habit habit, String languageCode);
}

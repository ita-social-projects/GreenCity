package greencity.service;

import greencity.dto.habitstatistic.HabitCreateDto;
import greencity.entity.Habit;
import greencity.entity.HabitDictionaryTranslation;
import greencity.entity.User;

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

    /**
     * Method assign {@link Habit} for user
     * @param habitId - id of habit user want to assign
     * @param user - user that assign habit
     * @return {@link HabitCreateDto}
     */
    HabitCreateDto assignHabitForUser(Long habitId, User user);
}

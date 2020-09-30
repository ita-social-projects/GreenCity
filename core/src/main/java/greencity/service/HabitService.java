package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitAssignDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface HabitService {
    /**
     * Method find {@link HabitTranslation} by {@link Habit} and languageCode.
     *
     * @return {@link HabitTranslation}
     * @author Kovaliv Taras
     */
    HabitTranslationDto getHabitTranslation(Habit habit, String languageCode);

    /**
     * Method find {@link Habit} by id.
     *
     * @return {@link Habit}
     * @author Kovaliv Taras
     */
    Habit getById(Long id);

    /**
     * Method assign {@link Habit} for user.
     *
     * @param habitId - id of habit user want to assign
     * @param user    - user that assign habit
     * @return {@link HabitAssignDto}
     */
    HabitAssignDto assignHabitForUser(Long habitId, User user);

    /**
     * Method find all {@link HabitDto}.
     *
     * @return list of {@link HabitDto}
     * @author Dovganyuk Taras
     */
    List<HabitDto> getAllHabitsDto();

    /**
     * Method returns all habits by language.
     *
     * @return Pageable of {@link HabitTranslationDto}
     * @author Dovganyuk Taras
     */
    PageableDto<HabitTranslationDto> getAllHabitsByLanguageCode(Pageable pageable, String language);
}

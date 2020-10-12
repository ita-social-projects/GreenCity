package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import org.springframework.data.domain.Pageable;

public interface HabitService {
    /**
     * Method find {@link HabitTranslation} by {@link Habit} and languageCode.
     *
     * @return {@link HabitTranslationDto}
     * @author Kovaliv Taras
     */
    HabitTranslationDto getHabitTranslation(Habit habit, String languageCode);

    /**
     * Method find {@link Habit} by id.
     *
     * @return {@link HabitDto}
     * @author Kovaliv Taras
     */
    HabitDto getById(Long id);

    /**
     * Method find all {@link HabitDto}.
     *
     * @return list of {@link HabitDto}
     * @author Dovganyuk Taras
     */
    PageableDto<HabitDto> getAllHabitsDto(Pageable pageable);

    /**
     * Method returns all habits by language.
     *
     * @return Pageable of {@link HabitTranslationDto}
     * @author Dovganyuk Taras
     */
    PageableDto<HabitTranslationDto> getAllHabitsByLanguageCode(Pageable pageable, String language);
}

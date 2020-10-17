package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import greencity.dto.habit.HabitVO;
import greencity.dto.habittranslation.HabitTranslationDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface HabitService {
    /**
     * Method find {@link HabitTranslationDto} by {@link HabitVO} and languageCode.
     *
     * @return {@link HabitTranslationDto}
     * @author Kovaliv Taras
     */
    HabitTranslationDto getHabitTranslation(HabitVO habit, String languageCode);

    /**
     * Method find Habit by id.
     *
     * @return {@link HabitVO}
     * @author Kovaliv Taras
     */
    HabitVO getById(Long id);

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

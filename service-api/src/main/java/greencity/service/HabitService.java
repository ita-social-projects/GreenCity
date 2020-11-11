package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.habit.HabitDto;
import org.springframework.data.domain.Pageable;

public interface HabitService {
    /**
     * Method finds {@code Habit} by id and language code.
     *
     * @param id           {@code Habit} id.
     * @param languageCode - language code.
     * @return {@link HabitDto}.
     */
    HabitDto getByIdAndLanguageCode(Long id, String languageCode);

    /**
     * Method returns all {@code Habit}'s by language code.
     *
     * @param pageable - instance of {@link Pageable}.
     * @param language - language code.
     * @return Pageable of {@link HabitDto}.
     */
    PageableDto<HabitDto> getAllHabitsByLanguageCode(Pageable pageable, String language);
}

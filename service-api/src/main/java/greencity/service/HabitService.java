package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habit.HabitDto;
import greencity.entity.Habit;
import greencity.entity.HabitTranslation;
import greencity.entity.User;
import org.springframework.data.domain.Pageable;
import java.util.List;

import java.util.List;

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

    /**
     * Method returns shopping list in specific language by habit id.
     *
     * @return list {@link GoalDto}.
     * @author Dmytro Khonko
     */
    List<GoalDto> getShoppingListForHabit(Long habitId, String lang);

    /**
     * Method that find all habit's translations by language code and tags.
     *
     * @param pageable     {@link Pageable}
     * @param tags         {@link List} of {@link String} tags
     * @param languageCode language code {@link String}
     *
     * @return {@link PageableDto} of {@link HabitDto}.
     * @author Markiyan Derevetskyi
     */
    PageableDto<HabitDto> getAllByTagsAndLanguageCode(Pageable pageable, List<String> tags, String languageCode);
}

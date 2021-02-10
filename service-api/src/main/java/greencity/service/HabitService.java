package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.goal.GoalDto;
import greencity.dto.habit.HabitDto;
import org.springframework.data.domain.Pageable;
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

    /**
     * Method that add Goal To Habit by habit id and goal id.
     * 
     * @author Marian Diakiv
     */
    void addGoalToHabit(Long habitId, Long goalId);

    /**
     * Method for deleting the {@link GoalDto} instance by its id.
     *
     * @param goalId  - {@link GoalDto} instance id which will be deleted.
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted.
     * @author Marian Diakiv
     */
    void deleteGoal(Long habitId, Long goalId);

    /**
     * Method deletes all {@link GoalDto} by list of ids.
     *
     * @param listId  list of id {@link GoalDto}
     * @param habitId - {@link HabitDto} the id of the instance from which it will
     *                be deleted. return list of id {@link GoalDto}
     * @author Marian Diakiv
     */
    List<Long> deleteAllGoalByListOfId(Long habitId, List<Long> listId);

    /**
     * Method add all {@link GoalDto} by list of ids.
     *
     * @param listId  list of id {@link GoalDto}
     * @param habitId - {@link HabitDto} the id of the instance to which it will be
     *                added return list of id {@link GoalDto}
     * @author Marian Diakiv
     */
    List<Long> addAllGoalByListOfId(Long habitId, List<Long> listId);
}

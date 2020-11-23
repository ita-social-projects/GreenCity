package greencity.repository;

import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.entity.Goal;
import greencity.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
@Repository
public interface HabitRepo extends JpaRepository<Habit, Long> {

//    @Query (nativeQuery = true, value = " SELECT gt.content as text, hg.goal_id FROM habit_goals AS hg "
//            + " JOIN goal_translations AS gt ON gt.goal_id = hg.goal_id "
//            + " JOIN languages AS l ON l.id = gt.language_id "
//            + " WHERE hg.goal_id IS NOT NULL AND "
//            + " hg.habit_id = :habitId AND l.code = :lang ")
    @Query ("SELECT g FROM Goal g JOIN HabitGoal hg ON hg.habit.id = :habitId")
    List<Goal> getShoppingList(@Param(value = "habitId") Long habitId);
}
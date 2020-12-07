package greencity.repository;

import greencity.dto.goal.ShoppingListDtoResponse;
import greencity.entity.Goal;
import greencity.entity.Habit;
import greencity.entity.localization.GoalTranslation;
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
}
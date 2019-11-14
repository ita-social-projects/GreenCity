package greencity.repository;

import greencity.entity.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
public interface HabitRepo extends JpaRepository<Habit, Long> {
}

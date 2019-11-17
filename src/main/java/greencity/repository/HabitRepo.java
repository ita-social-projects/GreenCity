package greencity.repository;

import greencity.entity.Habit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
public interface HabitRepo extends JpaRepository<Habit, Long> {
    /**
     * Find {@link Habit} by user.
     *
     * @param userId .
     * @return List Habit's.
     * @author Volodymyr Turko
     */
    List<Habit> findAllByUserId(Long userId);
}
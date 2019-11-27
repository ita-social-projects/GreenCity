package greencity.repository;

import greencity.entity.Habit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Habit} entity.
 */
@Repository
public interface HabitRepo extends JpaRepository<Habit, Long> {
    /**
     * Find {@link Habit} by user.
     *
     * @param userId .
     * @return List Habit's.
     * @author Volodymyr Turko
     */
    Optional<List<Habit>> findAllByUserId(Long userId);
}
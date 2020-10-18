package greencity.repository;

import greencity.entity.Habit;
import greencity.entity.HabitFact;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitFactRepo extends JpaRepository<HabitFact, Long> {
    /**
     * Method finds all {@link HabitFact}'s by {@link Habit} id.
     *
     * @param habitId {@link Habit} instance.
     */
    List<HabitFact> findAllByHabitId(Long habitId);
}

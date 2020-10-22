package greencity.repository;

import greencity.entity.Advice;
import greencity.entity.Habit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdviceRepo extends JpaRepository<Advice, Long> {
    /**
     * Method finds all {@link Advice}'s by {@link Habit} id.
     *
     * @param habitId {@link Habit} instance.
     */
    List<Advice> findAllByHabitId(Long habitId);
}

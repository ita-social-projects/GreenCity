package greencity.repository;

import greencity.entity.HabitStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link HabitStatistic} entity.
 */
@Repository
public interface HabitStatisticRepo extends JpaRepository<HabitStatistic, Long> {
}

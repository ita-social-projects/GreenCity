package greencity.repository;

import greencity.entity.BreakTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link BreakTime} entity.
 */
@Repository
public interface BreakTimeRepo extends JpaRepository<BreakTime, Long> {
}

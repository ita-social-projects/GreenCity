package greencity.repository;

import greencity.entity.BreakTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BreakTimeRepo extends JpaRepository<BreakTime, Long> {
}

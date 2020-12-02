package greencity.repository;

import greencity.entity.HabitFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitFactRepo extends JpaRepository<HabitFact, Long> {
}

package greencity.repository;

import greencity.entity.DailyFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyFactRepo extends JpaRepository<DailyFact, Long> {
    Optional<DailyFact> findByEmail(String email);

    boolean existsByEmail(String email);
}

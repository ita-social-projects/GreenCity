package greencity.repository;

import greencity.entity.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Estimate} entity.
 */
@Repository
public interface EstimateRepo extends JpaRepository<Estimate, Long> {
}

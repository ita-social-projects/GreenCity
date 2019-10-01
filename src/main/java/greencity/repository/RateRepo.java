package greencity.repository;

import greencity.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Rate} entity.
 */
@Repository
public interface RateRepo extends JpaRepository<Rate, Long> {
}

package greencity.repository;

import greencity.entity.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpeningHoursRepo extends JpaRepository<OpeningHours, Long> {
}

package greencity.repositories;

import greencity.entities.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHoursRepo extends JpaRepository<OpeningHours, Long> {}

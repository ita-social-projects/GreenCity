package greencity.repository;

import greencity.entity.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHoursRepo extends JpaRepository<OpeningHours, Long> {}

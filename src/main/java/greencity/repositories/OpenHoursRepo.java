package greencity.repositories;

import greencity.entities.OpeningHours;
import greencity.entities.Place;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpenHoursRepo extends JpaRepository<OpeningHours, Long> {
    List<OpeningHours> findAllByPlaceId(Long placeId);
}

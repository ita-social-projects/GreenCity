package greencity.repositories;

import greencity.entities.Place;
import greencity.entities.enums.PlaceStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceRepo extends JpaRepository<Place, Long> {
    List<Place> findPlacesByStatus(PlaceStatus status);
}

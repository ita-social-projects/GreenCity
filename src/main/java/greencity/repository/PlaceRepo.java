package greencity.repository;

import greencity.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepo extends JpaRepository<Place, Long> {}

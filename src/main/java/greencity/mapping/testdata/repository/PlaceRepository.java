package greencity.mapping.testdata.repository;

import greencity.mapping.testdata.entities.Place;
import greencity.mapping.testdata.entities.Visitor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Integer> {
    List<Place> findPlacesByVisitors(Visitor visitor);
}

package greencity.mapping.testdata.repository;

import greencity.mapping.testdata.entities.Place;
import greencity.mapping.testdata.entities.Visitor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitor, Integer> {
    List<Visitor> findVisitorByPlaces(Place place);
}

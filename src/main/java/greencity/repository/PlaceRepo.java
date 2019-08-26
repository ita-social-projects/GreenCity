package greencity.repository;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import org.hibernate.annotations.OrderBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Provides an interface to manage {@link Place} entity. */
@Repository
public interface PlaceRepo extends JpaRepository<Place, Long> {

    /**
     * Finds all places related to the given {@code PlaceStatus}.
     *
     * @param status to find by.
     * @return a list of places with the given {@code PlaceStatus}.
     */
    List<Place> findAllByStatusOrderByModifiedDateDesc(PlaceStatus status);
}

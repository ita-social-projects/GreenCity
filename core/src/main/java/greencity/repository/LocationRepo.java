package greencity.repository;

import greencity.entity.Location;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link Location} entity.
 */
@Repository
public interface LocationRepo extends JpaRepository<Location, Long> {
    /**
     * Method return a location {@code Location} which has not a {@code Place}.
     *
     * @param lat latitude of point of the map
     * @param lng longitude of point of the map
     * @return a {@link Optional} of {@code Location}
     * @author Kateryna Horokh.
     */
    Optional<Location> findByLatAndLng(Double lat, Double lng);
}

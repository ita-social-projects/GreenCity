package greencity.repository;

import greencity.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     * @return a {@code Location}
     * @author Kateryna Horokh.
     */
    @Query(value = "select l.address from Location l where l.lat = ?1 "
        + "and l.lng = ?2 and l.place_id is not null", nativeQuery = true)
    Location findByLatAndLng(Double lat, Double lng);
}

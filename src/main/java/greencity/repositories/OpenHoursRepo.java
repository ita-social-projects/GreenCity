package greencity.repositories;

import greencity.entities.OpeningHours;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Provides an interface to manage {@link OpeningHours} entity.
 * */
@Repository
public interface OpenHoursRepo extends JpaRepository<OpeningHours, Long> {
    /**
     * Finds all {@code OpeningHours} records related to the specified {@code Place}.
     *
     * @param placeId the ID of the place.
     * @return a list of the {@code OpeningHours} for the place.
     */
    List<OpeningHours> findAllByPlaceId(Long placeId);
}

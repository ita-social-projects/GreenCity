package greencity.repository;

import greencity.entity.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLocationRepo extends JpaRepository<UserLocation, Long> {
    /**
     * Gets the UserLocation by latitude and longitude.
     */
    Optional<UserLocation> getUserLocationByLatitudeAndLongitude(Double latitude, Double longitude);

    /**
     * Find and return all cities for all users.
     *
     * @return {@link List} of {@link String} of cities
     **/
    @Query(value = "SELECT u.userLocation FROM User u WHERE u.id =:userId")
    Optional<UserLocation> findAllUsersCities(Long userId);
}

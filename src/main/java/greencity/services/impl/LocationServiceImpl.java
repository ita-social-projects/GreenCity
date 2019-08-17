package greencity.services.impl;

import greencity.entities.Location;
import greencity.exceptions.NotFoundException;
import greencity.repositories.LocationRepo;
import greencity.services.LocationService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Location entity.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepo locationRepo;

    /**
     * Find all locations from DB.
     *
     * @return List of categories.
     */
    @Override
    public List<Location> findAll() {
        return locationRepo.findAll();
    }

    /**
     * Find Location entity by id.
     *
     * @param id - Location id.
     * @return Location entity.
     */
    @Override
    public Location findById(Long id) {
        return locationRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found with id " + id));
    }

    /**
     * Save Location to DB.
     *
     * @param location - entity of Location.
     * @return saved Location.
     */
    @Override
    public Location save(Location location) {
        return locationRepo.saveAndFlush(location);
    }

    /**
     * Update Location in DB.
     *
     * @param id - Location id.
     * @param location - Location entity.
     * @return Location updated entity.
     */
    @Override
    public Location update(Long id, Location location) {
        Location updatable = findById(id);

        updatable.setLat(location.getLat());
        updatable.setLng(location.getLng());
        updatable.setAddress(location.getAddress());
        updatable.setPlace(location.getPlace());

        return locationRepo.saveAndFlush(updatable);
    }

    /**
     * Delete entity from DB.
     *
     * @param location - Location entity.
     */
    @Override
    public void delete(Location location) {
        locationRepo.delete(location);
    }
}

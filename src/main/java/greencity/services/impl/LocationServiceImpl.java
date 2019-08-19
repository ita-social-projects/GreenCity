package greencity.services.impl;

import greencity.entities.Location;
import greencity.exceptions.NotFoundException;
import greencity.repositories.LocationRepo;
import greencity.services.LocationService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for Location entity.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepo locationRepo;

    /**
     * Find all locations from DB.
     *
     * @return List of categories.
     * @author Nazar Vladyka
     */
    @Override
    public List<Location> findAll() {
        log.info("in findAll()");
        return locationRepo.findAll();
    }

    /**
     * Find Location entity by id.
     *
     * @param id - Location id.
     * @return Location entity.
     * @author Nazar Vladyka
     */
    @Override
    public Location findById(Long id) {
        log.info("in findById()");
        return locationRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found with id " + id));
    }

    /**
     * Save Location to DB.
     *
     * @param location - entity of Location.
     * @return saved Location.
     * @author Nazar Vladyka
     */
    @Override
    public Location save(Location location) {
        log.info("in save()");
        return locationRepo.saveAndFlush(location);
    }

    /**
     * Update Location in DB.
     *
     * @param id - Location id.
     * @param location - Location entity.
     * @return Location updated entity.
     * @author Nazar Vladyka
     */
    @Override
    public Location update(Long id, Location location) {
        log.info("in update() start");
        Location updatable = findById(id);

        updatable.setLat(location.getLat());
        updatable.setLng(location.getLng());
        updatable.setAddress(location.getAddress());
        updatable.setPlace(location.getPlace());

        log.info("in update() save updatable value");
        return locationRepo.saveAndFlush(updatable);
    }

    /**
     * Delete entity from DB.
     *
     * @param location - Location entity.
     * @author Nazar Vladyka
     */
    @Override
    public void delete(Location location) {
        log.info("in delete()");
        locationRepo.delete(location);
    }
}

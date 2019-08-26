package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.entity.Location;
import greencity.exception.NotFoundException;
import greencity.repository.LocationRepo;
import greencity.service.LocationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
        log.info(LogMessage.IN_FIND_ALL);

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
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return locationRepo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.LOCATION_NOT_FOUND_BY_ID + id));
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
        log.info("in save(Location location), {}", location);

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
        log.info(LogMessage.IN_UPDATE, location);

        Location updatable = findById(id);

        updatable.setLat(location.getLat());
        updatable.setLng(location.getLng());
        updatable.setAddress(location.getAddress());
        updatable.setPlace(location.getPlace());

        return locationRepo.save(updatable);
    }

    /**
     * Delete entity from DB by id.
     *
     * @param id - Location id.
     * @author Nazar Vladyka
     */
    @Override
    public void deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        findById(id);

        locationRepo.deleteById(id);
    }
}

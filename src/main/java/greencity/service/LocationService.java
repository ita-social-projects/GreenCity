package greencity.service;

import greencity.entity.Location;
import java.util.List;

/**
 * Provides the interface to manage {@code Location} entity.
 */
public interface LocationService {
    /**
     * Update Location in DB.
     *
     * @param id       - Location id.
     * @param location - Location entity.
     * @return Location updated entity.
     */
    Location update(Long id, Location location);

    /**
     * Find Location entity by id.
     *
     * @param id - Location id.
     * @return Location entity.
     */
    Location findById(Long id);

    /**
     * Find all locations from DB.
     *
     * @return List of categories.
     */
    List<Location> findAll();

    /**
     * Delete entity from DB by id.
     *
     * @param id - Location id.
     * @return id of deleted Location.
     */
    Long deleteById(Long id);

    /**
     * Save Location to DB.
     *
     * @param location - entity of Location.
     * @return saved Location.
     */
    Location save(Location location);

    /**
     * The method which return a {@code Location} depends on the map position.
     *
     * @param lat latitude of point of the map
     * @param lng longitude of point of the map
     * @return a {@code Location}
     * @author Kateryna Horokh.
     */
    Location findByLatAndLng(Double lat, Double lng);
}

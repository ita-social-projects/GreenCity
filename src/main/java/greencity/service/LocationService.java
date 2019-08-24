package greencity.service;

import greencity.entity.Location;

import java.util.List;

/**
 * Provides the interface to manage {@code Location} entity.
 * */
public interface LocationService {

    Location save(Location location);

    Location update(Long id, Location location);

    Location findById(Long id);

    List<Location> findAll();

    void deleteById(Long id);
}

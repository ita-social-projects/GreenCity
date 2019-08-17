package greencity.services;

import greencity.entities.Location;
import java.util.List;

public interface LocationService {
    List<Location> findAll();

    Location findById(Long id);

    Location save(Location location);

    Location update(Long id, Location location);

    void delete(Location location);
}

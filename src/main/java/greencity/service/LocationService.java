package greencity.service;

import greencity.entity.Location;
import java.util.List;

public interface LocationService {

    Location save(Location location);

    Location update(Location location);

    Location findById(Long id);

    List<Location> findAll();

    void deleteById(Long id);
}

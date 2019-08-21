package greencity.service;

import greencity.dto.location.LocationByBoundsDto;
import greencity.dto.location.MapBoundsDto;
import greencity.entity.Location;
import java.util.List;

public interface LocationService {

    Location save(Location location);

    Location update(Location location);

    Location findById(Long id);

    List<Location> findAll();

    void deleteById(Long id);
    List<LocationByBoundsDto> findPlacesLocationByMapsBounds(MapBoundsDto mapBoundsDto);
}

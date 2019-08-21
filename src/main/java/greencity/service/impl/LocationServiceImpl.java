package greencity.service.impl;

import greencity.dto.location.LocationByBoundsDto;
import greencity.dto.location.MapBoundsDto;
import greencity.entity.Location;
import greencity.exception.BadIdException;
import greencity.repository.LocationRepo;
import greencity.service.LocationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

  private LocationRepo locationRepo;

  @Override
  public Location save(Location location) {
    return locationRepo.save(location);
  }

  @Override
  public Location update(Location place) {
    return null;
  }

  @Override
  public Location findById(Long id) {
    return locationRepo
        .findById(id)
        .orElseThrow(() -> new BadIdException("No location with this id: " + id));
  }

  @Override
  public List<Location> findAll() {
    return locationRepo.findAll();
  }

  @Override
  public void deleteById(Long id) {
    Location location = findById(id);
    locationRepo.delete(location);
  }

  @Override
  public List<LocationByBoundsDto> findPlacesLocationByMapsBounds(MapBoundsDto mapBoundsDto) {
    List<Location> list =
        locationRepo.findLocationsByMapsBounds(
            mapBoundsDto.getNorthEastLat(),
            mapBoundsDto.getNorthEastLng(),
            mapBoundsDto.getSouthWestLat(),
            mapBoundsDto.getSouthWestLng());
    return list.stream().map(LocationByBoundsDto::new).collect(Collectors.toList());
  }
}

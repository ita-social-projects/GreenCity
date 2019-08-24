package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.Location;
import greencity.exception.BadIdException;
import greencity.repository.LocationRepo;
import greencity.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/** The class provides implementation of the {@code LocationService}. */
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
                .orElseThrow(() -> new BadIdException(ErrorMessage.LOCATION_NOT_FOUND_BY_ID + id));
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
}

package greencity.service.impl;

import greencity.entity.Location;
import greencity.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LocationServiceImpl implements LocationService {


    @Override
    public Location save(Location place) {
        return null;
    }

    @Override
    public Location update(Location place) {
        return null;
    }

    @Override
    public Location findById(Long id) {
        return null;
    }

    @Override
    public List<Location> findAll() {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}

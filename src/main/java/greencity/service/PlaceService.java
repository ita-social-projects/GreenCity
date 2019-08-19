package greencity.service;

import greencity.dto.place.PlaceAddDto;
import greencity.entity.Place;

import java.util.List;

public interface PlaceService {

    Place save(PlaceAddDto dto);

    Place update(Place place);

    Place findById(Long id);

    List<Place> findAll();

    void deleteById(Long id);

    Place findByAddress(String address);
}

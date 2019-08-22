package greencity.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.Place;

import java.util.List;

public interface PlaceService {

    Place save(PlaceAddDto dto);

    Place update(Place place);

    Place findById(Long id);

    List<Place> findAll();

    Boolean deleteById(Long id);
}


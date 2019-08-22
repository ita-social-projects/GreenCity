package greencity.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import greencity.dto.place.PlaceAddDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;

import java.util.List;

/**
 * Provides the interface to manage {@code Place} entity.
 * */
public interface PlaceService {

    Place save(PlaceAddDto dto);

    Place update(Place place);

    Place findById(Long id);

    List<Place> findAll();

    Boolean deleteById(Long id);

    List<Place> getPlacesByStatus(PlaceStatus placeStatus);
}

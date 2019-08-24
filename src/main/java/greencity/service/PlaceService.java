package greencity.service;

import greencity.dto.location.MapBoundsDto;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.PlaceByBoundsDto;
import greencity.entity.Place;

import java.util.List;

public interface PlaceService {

    Place save(PlaceAddDto dto);

    Place update(Place place);

    Place findById(Long id);

    List<Place> findAll();

    void deleteById(Long id);

    Place findByAddress(String address);

    List<PlaceByBoundsDto> findPlacesByMapsBounds(MapBoundsDto mapBoundsDto);
}

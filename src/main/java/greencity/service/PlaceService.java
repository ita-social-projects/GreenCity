package greencity.service;

import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;
import greencity.dto.location.MapBoundsDto;

/** Provides the interface to manage {@code Place} entity. */
public interface PlaceService {

    List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus);

    Place updateStatus(Long placeId, PlaceStatus placeStatus);

    Place findById(Long id);

    Place save(Place place);

    List<PlaceByBoundsDto> findPlacesByMapsBounds(MapBoundsDto mapBoundsDto);

}

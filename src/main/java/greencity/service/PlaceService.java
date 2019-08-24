package greencity.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import greencity.dto.place.PlaceAddDto;
import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;

import java.util.List;

/** Provides the interface to manage {@code Place} entity. */
public interface PlaceService {

    List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus);

    Place updateStatus(Long placeId, PlaceStatus placeStatus);

    Place findById(Long id);

    Place save(Place place);

    Place save(PlaceAddDto dto);

    Place update(Place place);

    List<Place> findAll();

    Boolean deleteById(Long id);
}

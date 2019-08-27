package greencity.service;

import greencity.dto.place.AdminPlaceDto;
import greencity.dto.place.PlaceStatusDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import java.util.List;

/** Provides the interface to manage {@code Place} entity. */
public interface PlaceService {

    List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus);

    PlaceStatusDto updateStatus(PlaceStatusDto dto);

    Place findById(Long id);
}

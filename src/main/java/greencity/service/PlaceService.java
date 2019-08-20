package greencity.service;

import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;

public interface PlaceService {
    Place updateStatus(Long placeId, PlaceStatus placeStatus);

    Place findById(Long id);

    Place save(Place place);
}

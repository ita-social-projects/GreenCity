package greencity.services;

import greencity.entities.Place;
import greencity.entities.enums.PlaceStatus;

public interface PlaceService {
    Place updateStatus(Long placeId, PlaceStatus placeStatus);

    Place findById(Long id);

    Place save(Place place);
}

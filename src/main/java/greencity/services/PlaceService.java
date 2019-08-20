package greencity.services;

import greencity.entities.Place;
import greencity.entities.enums.PlaceStatus;
import java.util.List;

public interface PlaceService {
    List<Place> getPlacesByStatus(PlaceStatus placeStatus);
}

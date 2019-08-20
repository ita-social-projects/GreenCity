package greencity.services;

import greencity.entities.Place;
import greencity.entities.enums.PlaceStatus;
import java.util.List;

/**
 * Provides the interface to manage {@code Place} entity.
 * */
public interface PlaceService {
    /**
     * Finds all {@code Place} with status {@code PlaceStatus}.
     *
     * @param placeStatus a value of {@link PlaceStatus} enum.
     * @return a list of {@code Place} with the given {@code placeStatus}
     */
    List<Place> getPlacesByStatus(PlaceStatus placeStatus);
}

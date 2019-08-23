package greencity.service;

import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
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
    List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus);
}

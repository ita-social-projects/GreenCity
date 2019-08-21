package greencity.services;

import greencity.entities.OpeningHours;
import greencity.entities.Place;
import java.util.List;

/**
 * Provides the interface to manage {@code OpeningHours} entity.
 * */
public interface OpenHoursService {

    /**
     * Finds all {@code OpeningHours} records related to the specified {@link
     * greencity.entities.Place}.
     *
     * @param place to find by.
     * @return a list of the {@code OpeningHours} for the place.
     */
    List<OpeningHours> getOpenHoursByPlace(Place place);
}

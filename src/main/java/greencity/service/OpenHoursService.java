package greencity.service;

import greencity.entity.OpeningHours;
import greencity.entity.Place;

import java.util.List;

/** Provides the interface to manage {@code OpeningHours} entity. */
public interface OpenHoursService {

    OpeningHours save(OpeningHours openingHours);

    OpeningHours findById(Long id);

    void deleteById(Long id);

    /**
     * Finds all {@code OpeningHours} records related to the specified {@link
     * greencity.entity.Place}.
     *
     * @param place to find by.
     * @return a list of the {@code OpeningHours} for the place.
     */
    List<OpeningHours> getOpenHoursByPlace(Place place);

    List<OpeningHours> findAll();

    OpeningHours update(Long id, OpeningHours updatedHours);
}

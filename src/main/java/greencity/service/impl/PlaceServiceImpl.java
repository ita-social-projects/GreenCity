package greencity.service.impl;

import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.NotFoundException;
import greencity.repository.PlaceRepo;
import greencity.service.DateTimeService;
import greencity.service.PlaceService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** The class provides implementation of the {@code PlaceService}. */
@Slf4j
@AllArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {
    /** Autowired repository. */
    private PlaceRepo placeRepo;

    /** {@inheritDoc} */
    @Override
    public List<Place> getPlacesByStatus(PlaceStatus placeStatus) {
        return placeRepo.findPlacesByStatus(placeStatus);
    }

    /**
     * Update status for the Place and set the time of modification.
     *
     * @param placeId - place id.
     * @param placeStatus - enum of Place status value.
     * @return saved Place entity.
     * @author Nazar Vladyka.
     */
    @Override
    public Place updateStatus(Long placeId, PlaceStatus placeStatus) {
        Place updatable =
                placeRepo
                        .findById(placeId)
                        .orElseThrow(
                                () -> new NotFoundException("Place not found with id " + placeId));

        updatable.setStatus(placeStatus);
        updatable.setModifiedDate(DateTimeService.getDateTime("Europe/Kiev"));

        log.info(
                "in updateStatus(Long placeId, PlaceStatus placeStatus) update place with id - {} and status - {}",
                placeId,
                placeStatus.toString());

        return placeRepo.saveAndFlush(updatable);
    }

    /**
     * Find place by it's id.
     *
     * @param id - place id.
     * @return Place entity.
     * @author Nazar Vladyka.
     */
    @Override
    public Place findById(Long id) {
        log.info("in findById(Long id), find place with id - {}", id);

        return placeRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Place not found with id " + id));
    }

    /**
     * Save place to database.
     *
     * @param place - Place entity.
     * @return saved Place entity.
     * @author Nazar Vladyka.
     */
    @Override
    public Place save(Place place) {
        log.info("in save(Place place), save place - {}", place.getName());

        return placeRepo.saveAndFlush(place);
    }
}

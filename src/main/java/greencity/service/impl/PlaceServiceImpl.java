package greencity.service.impl;

import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.NotFoundException;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class PlaceServiceImpl implements PlaceService {
    private final PlaceRepo placeRepo;

    /**
     * Get current date and time in zone.
     *
     * @param zoneId - zone id.
     * @return LocalDateTime object.
     * @author Nazar Vladyka.
     */
    public static LocalDateTime getDateTime(String zoneId) {
        log.info("in getDateTime(String zoneId), get time in timezone - {}", zoneId);

        return LocalDateTime.now(ZoneId.of(zoneId)).withSecond(0).withNano(0);
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
        Place updatable = findById(placeId);

        updatable.setStatus(placeStatus);
        updatable.setModifiedDate(PlaceServiceImpl.getDateTime("Europe/Kiev"));

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

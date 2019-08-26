package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.exception.NotFoundException;
import greencity.repository.OpenHoursRepo;
import greencity.service.OpenHoursService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/** The class provides implementation of the {@code OpenHoursService}. */
/** The class provides implementation of the {@code OpenHoursService}. */
@Slf4j
@AllArgsConstructor
@Service
public class OpenHoursServiceImpl implements OpenHoursService {

    /** Autowired repository. */
    private OpenHoursRepo hoursRepo;

    /**
     * {@inheritDoc}
     *
     * @author Roman Zahorui
     */
    public List<OpeningHours> getOpenHoursByPlace(Place place) {
        return hoursRepo.findAllByPlace(place);
    }

    /**
     * Save OpeningHours to DB.
     *
     * @param hours - entity of OpeningHours.
     * @return saved OpeningHours.
     * @author Nazar Vladyka
     */
    @Override
    public OpeningHours save(OpeningHours hours) {
        log.info("in save(OpeningHours hours), {}", hours);

        return hoursRepo.saveAndFlush(hours);
    }

    /**
     * Find all opening hours from DB.
     *
     * @return List of opening hours.
     * @author Nazar Vladyka
     */
    @Override
    public List<OpeningHours> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return hoursRepo.findAll();
    }

    /**
     * Find OpeningHours entity by id.
     *
     * @param id - OpeningHours id.
     * @return OpeningHours entity.
     * @author Nazar Vladyka
     */
    @Override
    public OpeningHours findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return hoursRepo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException(ErrorMessage.OPEN_HOURS_NOT_FOUND_BY_ID + id));
    }

    /**
     * Update OpeningHours in DB.
     *
     * @param id - OpeningHours id.
     * @param updatedHours - OpeningHours entity.
     * @return OpeningHours updated entity.
     * @author Nazar Vladyka
     */
    @Override
    public OpeningHours update(Long id, OpeningHours updatedHours) {
        log.info(LogMessage.IN_UPDATE, updatedHours);

        OpeningHours updatable = findById(id);

        updatable.setOpenTime(updatedHours.getOpenTime());
        updatable.setCloseTime(updatedHours.getCloseTime());
        updatable.setWeekDay(updatedHours.getWeekDay());
        updatable.setPlace(updatedHours.getPlace());

        return hoursRepo.save(updatable);
    }

    /**
     * Delete entity from DB by id.
     *
     * @param id - OpeningHours id.
     * @author Nazar Vladyka
     */
    @Override
    public void deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        findById(id);

        hoursRepo.deleteById(id);
    }
}

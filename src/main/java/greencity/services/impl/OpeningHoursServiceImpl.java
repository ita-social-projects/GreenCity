package greencity.services.impl;

import greencity.entities.OpeningHours;
import greencity.exceptions.NotFoundException;
import greencity.repositories.OpeningHoursRepo;
import greencity.services.OpeningHoursService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service implementation for OpeningHours entity.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
@Service
@AllArgsConstructor
@Slf4j
public class OpeningHoursServiceImpl implements OpeningHoursService {
    private final OpeningHoursRepo openingHoursRepo;

    /**
     * Find all opening hours from DB.
     *
     * @return List of opening hours.
     * @author Nazar Vladyka
     */
    @Override
    public List<OpeningHours> findAll() {
        log.info("in findAll()");
        return openingHoursRepo.findAll();
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
        log.info("in findById(Long id), with id - {} ", id);

        return openingHoursRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("OpeningHours not found with id " + id));
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

        return openingHoursRepo.saveAndFlush(hours);
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
        OpeningHours updatable = findById(id);

        updatable.setOpenTime(updatedHours.getOpenTime());
        updatable.setCloseTime(updatedHours.getCloseTime());
        updatable.setWeekDay(updatedHours.getWeekDay());
        updatable.setPlace(updatedHours.getPlace());

        log.info("in update(Long id, OpeningHours updatedHours), {}", updatedHours);

        return openingHoursRepo.saveAndFlush(updatable);
    }

    /**
     * Delete entity from DB.
     *
     * @param hours - OpeningHours entity.
     * @author Nazar Vladyka
     */
    @Override
    public void delete(OpeningHours hours) {
        log.info("in delete(OpeningHours hours), delete {}", hours);

        openingHoursRepo.delete(hours);
    }
}

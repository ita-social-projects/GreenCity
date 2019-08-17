package greencity.services.impl;

import greencity.entities.OpeningHours;
import greencity.exceptions.NotFoundException;
import greencity.repositories.OpeningHoursRepo;
import greencity.services.OpeningHoursService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for OpeningHours entity.
 *
 * @author Nazar Vladyka
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class OpeningHoursServiceImpl implements OpeningHoursService {
    private final OpeningHoursRepo openingHoursRepo;

    /**
     * Find all opening hours from DB.
     *
     * @return List of opening hours.
     */
    @Override
    public List<OpeningHours> findAll() {
        return openingHoursRepo.findAll();
    }

    /**
     * Find OpeningHours entity by id.
     *
     * @param id - OpeningHours id.
     * @return OpeningHours entity.
     */
    @Override
    public OpeningHours findById(Long id) {
        return openingHoursRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
    }

    /**
     * Save OpeningHours to DB.
     *
     * @param hours - entity of OpeningHours.
     * @return saved OpeningHours.
     */
    @Override
    public OpeningHours save(OpeningHours hours) {
        return openingHoursRepo.saveAndFlush(hours);
    }

    /**
     * Update OpeningHours in DB.
     *
     * @param id - OpeningHours id.
     * @param updatedHours - OpeningHours entity.
     * @return OpeningHours updated entity.
     */
    @Override
    public OpeningHours update(Long id, OpeningHours updatedHours) {
        OpeningHours updatable = findById(id);

        updatable.setOpenTime(updatedHours.getOpenTime());
        updatable.setCloseTime(updatable.getCloseTime());
        updatable.setWeekDay(updatable.getWeekDay());
        updatable.setPlace(updatable.getPlace());

        return openingHoursRepo.saveAndFlush(updatable);
    }

    /**
     * Delete entity from DB.
     *
     * @param hours - OpeningHours entity.
     */
    @Override
    public void delete(OpeningHours hours) {
        openingHoursRepo.delete(hours);
    }
}

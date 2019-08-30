package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.exception.NotFoundException;
import greencity.repository.OpenHoursRepo;
import greencity.service.OpenHoursService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public OpeningHours save(OpeningHours hours) {
        log.info(LogMessage.IN_SAVE, hours);

        return hoursRepo.saveAndFlush(hours);
    }

    /**
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public List<OpeningHours> findAll() {
        log.info(LogMessage.IN_FIND_ALL);

        return hoursRepo.findAll();
    }

    /**
     * {@inheritDoc}
     *
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
     * {@inheritDoc}
     *
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
     * {@inheritDoc}
     *
     * @author Nazar Vladyka
     */
    @Override
    public void deleteById(Long id) {
        log.info(LogMessage.IN_DELETE_BY_ID, id);

        findById(id);

        hoursRepo.deleteById(id);
    }
}

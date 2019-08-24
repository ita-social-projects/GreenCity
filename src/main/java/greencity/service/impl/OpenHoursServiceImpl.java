package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.exception.BadIdException;
import greencity.repository.OpenHoursRepo;
import greencity.service.OpenHoursService;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code OpenHoursService}.
 * */
@Service
@AllArgsConstructor
public class OpenHoursServiceImpl implements OpenHoursService {

    /** Autowired repository.*/
    private OpenHoursRepo hoursRepo;

    @Override
    public OpeningHours save(OpeningHours openingHours) {
        return hoursRepo.save(openingHours);
    }

    @Override
    public OpeningHours update(OpeningHours location) {
        return null;
    }

    @Override
    public OpeningHours findById(Long id) {
        return hoursRepo
                .findById(id)
                .orElseThrow(() -> new BadIdException(ErrorMessage.OPENING_HOURS_NOT_FOUND_BY_ID + id));
    }

    @Override
    public List<OpeningHours> findAll() {
        return hoursRepo.findAll();
    }

    @Override
    public void deleteById(Long id) {
        OpeningHours openingHours = findById(id);
        hoursRepo.delete(openingHours);
    }

    /**
     * {@inheritDoc}
     */
    public List<OpeningHours> getOpenHoursByPlace(Place place) {
        return hoursRepo.findAllByPlace(place);
    }
}

package greencity.service.impl;

import greencity.entity.OpeningHours;
import greencity.entity.Place;
import greencity.repository.OpenHoursRepo;
import greencity.service.OpenHoursService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code OpenHoursService}.
 * */
@AllArgsConstructor
@Service
public class OpenHoursServiceImpl implements OpenHoursService {

    /** Autowired repository.*/
    private OpenHoursRepo hoursRepo;

    /**
     * {@inheritDoc}
     */
    public List<OpeningHours> getOpenHoursByPlace(Place place) {
        return hoursRepo.findAllByPlace(place);
    }
}

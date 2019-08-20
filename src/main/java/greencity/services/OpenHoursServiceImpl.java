package greencity.services;

import greencity.entities.OpeningHours;
import greencity.repositories.OpenHoursRepo;
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
    public List<OpeningHours> getOpenHours(Long placeId) {
        return hoursRepo.findAllByPlaceId(placeId);
    }
}

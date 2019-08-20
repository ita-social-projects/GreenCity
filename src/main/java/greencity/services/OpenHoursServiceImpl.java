package greencity.services;

import greencity.entities.OpeningHours;
import greencity.repositories.OpenHoursRepo;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class OpenHoursServiceImpl implements OpenHoursService {

    private OpenHoursRepo hoursRepo;

    public List<OpeningHours> getOpenHours(Long placeId) {
        return hoursRepo.findAllByPlaceId(placeId);
    }
}

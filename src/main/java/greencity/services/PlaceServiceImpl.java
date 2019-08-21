package greencity.services;

import greencity.entities.Place;
import greencity.entities.enums.PlaceStatus;
import greencity.repositories.PlaceRepo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * The class provides implementation of the {@code PlaceService}.
 * */
@Slf4j
@AllArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {

    /** Autowired variables.*/
    private OpenHoursService hoursService;
    private UserService userService;

    /** Autowired repository.*/
    private PlaceRepo placeRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Place> getPlacesByStatus(PlaceStatus placeStatus) {
        List<Place> places = placeRepo.findPlacesByStatus(placeStatus);
        places.forEach(place -> place.setOpeningHours(hoursService.getOpenHoursByPlace(place)));
        places.forEach(place -> place.setAuthor(userService.getByAddedPlace(place)));
        return places;
    }
}

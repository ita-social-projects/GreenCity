package greencity.services;

import greencity.entities.Place;
import greencity.entities.enums.PlaceStatus;
import greencity.repositories.PlaceRepo;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {

    private OpenHoursService hoursService;
    private UserService userService;

    private PlaceRepo placeRepo;

    @Override
    public List<Place> getPlacesByStatus(PlaceStatus placeStatus) {
        List<Place> places = placeRepo.findPlacesByStatus(placeStatus);
        places.forEach(place -> place.setOpeningHours(hoursService.getOpenHours(place.getId())));
        places.forEach(place -> place.setAuthor(userService.findByAddedPlacesContains(place)));
        return places;
    }
}

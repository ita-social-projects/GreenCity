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

    /** Autowired repository. */
    private PlaceRepo placeRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Place> getPlacesByStatus(PlaceStatus placeStatus) {
        return placeRepo.findPlacesByStatus(placeStatus);
    }
}

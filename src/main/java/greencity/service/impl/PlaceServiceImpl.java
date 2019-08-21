package greencity.service.impl;

import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.repository.PlaceRepo;
import greencity.service.PlaceService;
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

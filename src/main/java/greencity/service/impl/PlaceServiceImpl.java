package greencity.service.impl;

import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.constant.LogMessage;
import greencity.dto.place.AdminPlaceDto;
import greencity.entity.Place;
import greencity.entity.enums.PlaceStatus;
import greencity.exception.NotFoundException;
import greencity.exception.PlaceStatusException;
import greencity.repository.PlaceRepo;
import greencity.service.DateTimeService;
import greencity.service.PlaceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/** The class provides implementation of {@code PlaceService} interface. */
@Slf4j
@AllArgsConstructor
@Service
public class PlaceServiceImpl implements PlaceService {

    /** Autowired repository. */
    private PlaceRepo placeRepo;
    /** Autowired mapper. */
    private ModelMapper modelMapper;

    /**
     * Finds all {@code Place} with status {@code PlaceStatus}.
     *
     * @param placeStatus a value of {@link PlaceStatus} enum.
     * @return a list of {@code Place} with the given {@code placeStatus}
     * @author Roman Zahorui
     */
    @Override
    public List<AdminPlaceDto> getPlacesByStatus(PlaceStatus placeStatus) {
        List<Place> places = placeRepo.getPlacesByStatus(placeStatus);
        return places.stream()
                .map(place -> modelMapper.map(place, AdminPlaceDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Update status for the Place and set the time of modification.
     *
     * @param placeId - place id.
     * @param placeStatus - enum of Place status value.
     * @return saved Place entity.
     * @author Nazar Vladyka.
     */
    @Override
    public Place updateStatus(Long placeId, PlaceStatus placeStatus) {
        log.info(LogMessage.IN_UPDATE_PLACE_STATUS, placeId, placeStatus.toString());

        Place updatable = findById(placeId);

        if (updatable.getStatus().equals(placeStatus)) {
            log.error(LogMessage.PLACE_STATUS_NOT_DIFFERENT, placeId, placeStatus);
            throw new PlaceStatusException(
                    ErrorMessage.PLACE_STATUS_NOT_DIFFERENT + updatable.getStatus());
        } else {
            updatable.setStatus(placeStatus);
            updatable.setModifiedDate(DateTimeService.getDateTime(AppConstant.UKRAINE_TIMEZONE));
        }

        return placeRepo.save(updatable);
    }

    /**
     * Find place by it's id.
     *
     * @param id - place id.
     * @return Place entity.
     * @author Nazar Vladyka.
     */
    @Override
    public Place findById(Long id) {
        log.info(LogMessage.IN_FIND_BY_ID, id);

        return placeRepo
                .findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.PLACE_NOT_FOUND_BY_ID + id));
    }
}

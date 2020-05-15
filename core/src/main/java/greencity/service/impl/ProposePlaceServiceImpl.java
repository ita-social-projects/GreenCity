package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.entity.DiscountValue;
import greencity.entity.Photo;
import greencity.entity.Place;
import greencity.exception.exceptions.BadRequestException;
import greencity.service.LocationService;
import greencity.service.PhotoService;
import greencity.service.ProposePlaceService;
import greencity.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link ProposePlaceService}.
 *
 * @author Marian Datsko
 */
@Service
public class ProposePlaceServiceImpl implements ProposePlaceService {

    private final SpecificationService specService;
    private final PhotoService photoService;
    private final LocationService locationService;

    /**
     * Constructor with parameters.
     *
     * @author Marian Datsko
     */
    @Autowired
    public ProposePlaceServiceImpl(SpecificationService specService,
                                   PhotoService photoService,
                                   LocationService locationService) {
        this.specService = specService;
        this.photoService = photoService;
        this.locationService = locationService;
    }

    /**
     * Method check if input location is new.
     */
    @Override
    public void checkLocationValues(LocationAddressAndGeoDto dto) {
        if (locationService.findByLatAndLng(dto.getLat(), dto.getLng()).isPresent()) {
            throw new BadRequestException(ErrorMessage.LOCATION_IS_PRESENT);
        }
    }

    /**
     * Method checks if input time is correct.
     */
    @Override
    public void checkInputTime(Set<OpeningHoursDto> dto) {
        dto.forEach(hours -> {
            if (hours.getOpenTime().getHour() > hours.getCloseTime().getHour()) {
                throw new BadRequestException(ErrorMessage.CLOSE_TIME_LATE_THAN_OPEN_TIME);
            }
            if (hours.getBreakTime() != null) {
                if (hours.getBreakTime().getStartTime().getHour() < hours.getOpenTime().getHour()
                        || hours.getBreakTime().getEndTime().getHour() > hours.getCloseTime().getHour()) {
                    throw new BadRequestException(ErrorMessage.WRONG_BREAK_TIME);
                }
            }
        });
    }

    /**
     * Method save Photo in object Place.
     */
    @Override
    public void savePhotosWithPlace(List<Photo> photos, Place place) {
        photos.forEach(photo -> {
            if (photoService.findByName(photo.getName()).isPresent()) {
                throw new BadRequestException(ErrorMessage.PHOTO_IS_PRESENT);
            }
            photo.setPlace(place);
        });
    }

    /**
     * Method save DiscountValue in object Place.
     */
    @Override
    public void saveDiscountValuesWithPlace(Set<DiscountValue> discountValues, Place place) {
        discountValues.forEach(disc -> {
            disc.setSpecification(specService.findByName(disc.getSpecification().getName()));
            disc.setPlace(place);
        });
    }
}

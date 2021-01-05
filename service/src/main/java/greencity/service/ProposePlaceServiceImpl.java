package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.descountvalue.DiscountValueVO;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.photo.PhotoVO;
import greencity.dto.place.PlaceVO;
import greencity.exception.exceptions.BadRequestException;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProposePlaceService}.
 *
 * @author Marian Datsko
 */
@Service
@AllArgsConstructor
public class ProposePlaceServiceImpl implements ProposePlaceService {
    private final SpecificationService specService;
    private final PhotoService photoService;
    private final LocationService locationService;

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
            if (hours.getBreakTime() != null && isWrongTime(hours)) {
                throw new BadRequestException(ErrorMessage.WRONG_BREAK_TIME);
            }
        });
    }

    private boolean isWrongTime(OpeningHoursDto hours) {
        return hours.getBreakTime().getStartTime().getHour() < hours.getOpenTime().getHour()
            || hours.getBreakTime().getEndTime().getHour() > hours.getCloseTime().getHour();
    }

    /**
     * Method save Photo in object Place.
     */
    @Override
    public void savePhotosWithPlace(List<PhotoVO> photos, PlaceVO place) {
        photos.forEach(photo -> {
            if (photoService.findByName(photo.getName()).isPresent()) {
                throw new BadRequestException(ErrorMessage.PHOTO_IS_PRESENT);
            }
            photo.setPlaceId(place.getId());
        });
    }

    /**
     * Method save DiscountValue in object Place.
     */
    @Override
    public void saveDiscountValuesWithPlace(Set<DiscountValueVO> discountValues, PlaceVO place) {
        discountValues.forEach(disc -> {
            disc.setSpecification(specService.findByName(disc.getSpecification().getName()));
            disc.setPlace(place);
        });
    }
}

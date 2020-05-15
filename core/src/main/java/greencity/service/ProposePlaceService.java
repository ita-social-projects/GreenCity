package greencity.service;

import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.entity.DiscountValue;
import greencity.entity.Photo;
import greencity.entity.Place;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ProposePlaceService {

    /**
     * Method check if input location is new.
     */
    void checkLocationValues(LocationAddressAndGeoDto dto);

    /**
     * Method checks if input time is correct.
     */
    void checkInputTime(Set<OpeningHoursDto> dto);

    /**
     * Method save Photo in object Place.
     */
    void savePhotosWithPlace(List<Photo> photos, Place place);

    /**
     * Method save DiscountValue in object Place.
     */
    void saveDiscountValuesWithPlace(Set<DiscountValue> discountValues, Place place);
}

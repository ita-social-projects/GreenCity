package greencity.service;

import greencity.dto.descountvalue.DiscountValueVO;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openinghours.OpeningHoursDto;
import greencity.dto.photo.PhotoVO;
import greencity.dto.place.PlaceVO;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

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
    void savePhotosWithPlace(List<PhotoVO> photos, PlaceVO place);

    /**
     * Method save DiscountValue in object Place.
     */
    void saveDiscountValuesWithPlace(Set<DiscountValueVO> discountValues, PlaceVO place);
}

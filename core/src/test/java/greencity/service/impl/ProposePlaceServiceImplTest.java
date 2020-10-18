package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.entity.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.service.LocationService;
import greencity.service.PhotoService;
import greencity.service.SpecificationService;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ProposePlaceServiceImplTest {

    @InjectMocks
    private ProposePlaceServiceImpl proposePlaceService;

    @Mock
    private SpecificationService specService;

    @Mock
    private PhotoService photoService;

    @Mock
    private LocationService locationService;

//    @Test
//    void checkLocationValues() {
//        Location location = ModelUtils.getLocation();
//        LocationAddressAndGeoDto address = ModelUtils.getLocationAddressAndGeoDto();
//
//        when(locationService.findByLatAndLng(12.12d, 12.12d)).thenReturn(Optional.of(location));
//        assertThrows(BadRequestException.class, () -> proposePlaceService.checkLocationValues(address));
//    }

    @Test
    void checkInputTime() {
        OpeningHoursDto openingHours = ModelUtils.getOpeningHoursDto();
        openingHours.setOpenTime(ModelUtils.getLocalTime().plusHours(1L));
        Set<OpeningHoursDto> dtos = Collections.singleton(openingHours);

        assertThrows(BadRequestException.class, () -> proposePlaceService.checkInputTime(dtos));
    }

    @Test
    void savePhotosWithPlace() {
        Place place = ModelUtils.getPlace();
        Photo photo = ModelUtils.getPhoto();

        Photo photoTest = ModelUtils.getPhoto();
        photoTest.setPlace(place);

        when(photoService.findByName(anyString())).thenReturn(Optional.empty());
        proposePlaceService.savePhotosWithPlace(Collections.singletonList(photo), place);
        assertEquals(Collections.singletonList(photoTest), Collections.singletonList(photo));
    }

    @Test
    void saveDiscountValuesWithPlace() {
        Place place = ModelUtils.getPlace();
        Specification specification = ModelUtils.getSpecification();

        DiscountValue discountValue = ModelUtils.getDiscountValue();
        discountValue.setSpecification(ModelUtils.getSpecification());

        DiscountValue discountValueTest = ModelUtils.getDiscountValue();
        discountValueTest.setSpecification(specification);
        discountValueTest.setPlace(place);

        when(specService.findByName(discountValue.getSpecification().getName())).thenReturn(specification);
        proposePlaceService.saveDiscountValuesWithPlace(Collections.singleton(discountValue), place);
        assertEquals(Collections.singleton(discountValueTest), Collections.singleton(discountValue));
    }
}
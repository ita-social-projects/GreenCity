package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.breaktime.BreakTimeDto;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.entity.*;
import greencity.exception.exceptions.BadRequestException;
import greencity.service.LocationService;
import greencity.service.PhotoService;
import greencity.service.SpecificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

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

    @Test
    void checkLocationValues() {
        Location location = Location.builder()
                .address("address")
                .lng(12.12d)
                .lat(12.12d)
                .build();

        LocationAddressAndGeoDto address = LocationAddressAndGeoDto.builder()
                .address("address")
                .lat(12.12d)
                .lng(12.12d)
                .build();

        when(locationService.findByLatAndLng(12.12d, 12.12d)).thenReturn(Optional.of(location));
        assertThrows(BadRequestException.class, () -> proposePlaceService.checkLocationValues(address));
    }

    @Test
    void checkInputTime() {
        OpeningHoursDto openingHours = new OpeningHoursDto();
        openingHours.setOpenTime(LocalTime.of(8, 20, 45, 342123342));
        openingHours.setCloseTime(LocalTime.of(7, 20, 45, 342123342));
        openingHours.setBreakTime(BreakTimeDto.builder()
                .startTime(LocalTime.of(7, 20, 45, 342123342))
                .endTime(LocalTime.of(7, 20, 45, 342123342))
                .build());
        openingHours.setWeekDay(DayOfWeek.MONDAY);

        assertThrows(BadRequestException.class, () -> proposePlaceService.checkInputTime(Collections.singleton(openingHours)));
    }

    @Test
    void savePhotosWithPlace() {
        Place place = ModelUtils.getPlace();
        Photo photo = Photo.builder()
                .id(1L)
                .name("photo")
                .build();

        Photo photoTest = Photo.builder()
                .id(1L)
                .name("photo")
                .place(place)
                .build();

        when(photoService.findByName(anyString())).thenReturn(Optional.empty());
        proposePlaceService.savePhotosWithPlace(Collections.singletonList(photo), place);
        assertEquals(Collections.singletonList(photoTest), Collections.singletonList(photo));
    }

    @Test
    void saveDiscountValuesWithPlace() {
        Place place = ModelUtils.getPlace();
        Specification specification = Specification.builder().id(1L).name("specification").build();
        DiscountValue discountValue = DiscountValue.builder()
                .id(1L)
                .value(11)
                .specification(specification)
                .build();

        DiscountValue discountValueTest = DiscountValue.builder()
                .id(1L)
                .value(11)
                .place(place)
                .specification(specification)
                .build();

        when(specService.findByName(discountValue.getSpecification().getName())).thenReturn(specification);
        proposePlaceService.saveDiscountValuesWithPlace(Collections.singleton(discountValue), place);
        assertEquals(Collections.singleton(discountValueTest), Collections.singleton(discountValue));
    }
}
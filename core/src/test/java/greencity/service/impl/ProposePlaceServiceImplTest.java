package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.LocationVO;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.specification.SpecificationVO;
import greencity.entity.DiscountValue;
import greencity.entity.Photo;
import greencity.entity.Place;
import greencity.entity.Specification;
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
import org.modelmapper.ModelMapper;
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

    @Mock
    private ModelMapper modelMapper;

    @Test
    void checkLocationValues() {
        LocationVO location = LocationVO.builder()
            .address("address")
            .lng(12.12d)
            .lat(12.12d)
            .build();
        LocationAddressAndGeoDto address = ModelUtils.getLocationAddressAndGeoDto();

        when(locationService.findByLatAndLng(12.12d, 12.12d)).thenReturn(Optional.of(location));
        assertThrows(BadRequestException.class, () -> proposePlaceService.checkLocationValues(address));
    }

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
        SpecificationVO specificationVO = new SpecificationVO(1L, "specification");


        DiscountValue discountValue = ModelUtils.getDiscountValue();
        discountValue.setSpecification(ModelUtils.getSpecification());

        DiscountValue discountValueTest = ModelUtils.getDiscountValue();
        discountValueTest.setSpecification(modelMapper.map(specificationVO, Specification.class));
        discountValueTest.setPlace(place);

        when(specService.findByName(discountValue.getSpecification().getName())).thenReturn(specificationVO);
        proposePlaceService.saveDiscountValuesWithPlace(Collections.singleton(discountValue), place);
        assertEquals(Collections.singleton(discountValueTest), Collections.singleton(discountValue));
    }
}
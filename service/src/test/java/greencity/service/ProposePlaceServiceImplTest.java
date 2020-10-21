package greencity.service;

import greencity.ModelUtils;
import greencity.dto.descountvalue.DiscountValueVO;
import greencity.dto.location.LocationAddressAndGeoDto;
import greencity.dto.location.LocationVO;
import greencity.dto.openhours.OpeningHoursDto;
import greencity.dto.photo.PhotoVO;
import greencity.dto.place.PlaceVO;
import greencity.dto.specification.SpecificationVO;
import greencity.exception.exceptions.BadRequestException;
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
        PhotoVO photoVO = new PhotoVO();
        PlaceVO placeVO = new PlaceVO();
        placeVO.setId(1L);
        when(photoService.findByName(anyString())).thenReturn(Optional.empty());
        proposePlaceService.savePhotosWithPlace(Collections.singletonList(photoVO), placeVO);
        assertEquals(placeVO.getId(), photoVO.getPlaceId());
    }

    @Test
    void saveDiscountValuesWithPlace() {
        SpecificationVO specificationVO = new SpecificationVO();
        PlaceVO placeVO = new PlaceVO();
        placeVO.setId(1L);
        DiscountValueVO discountValueVO = new DiscountValueVO();
        DiscountValueVO discountValueVOTest = new DiscountValueVO();
        discountValueVOTest.setPlace(placeVO);
        discountValueVOTest.setSpecification(specificationVO);
        discountValueVO.setSpecification(specificationVO);
        when(specService.findByName(anyString())).thenReturn(specificationVO);
        proposePlaceService.saveDiscountValuesWithPlace(Collections.singleton(discountValueVO),
            placeVO);
        assertEquals(discountValueVO.getPlace().getId(), discountValueVOTest.getPlace().getId());
    }
}

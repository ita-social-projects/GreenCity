package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.AddressDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.geocoding.AddressResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressLatLngResponseMapperTest {
    @InjectMocks
    private AddressLatLngResponseMapper mapper;

    @Test
    void convert() {
        AddressDto expected = ModelUtils.getAddressDto();
        AddressLatLngResponse response = AddressLatLngResponse
            .builder()
            .latitude(expected.getLatitude())
            .longitude(expected.getLongitude())
            .addressUa(AddressResponse
                .builder()
                .street(expected.getStreetUa())
                .houseNumber(expected.getHouseNumber())
                .city(expected.getCityUa())
                .region(expected.getRegionUa())
                .country(expected.getCountryUa())
                .formattedAddress(expected.getFormattedAddressUa())
                .build())
            .addressEn(AddressResponse
                .builder()
                .street(expected.getStreetEn())
                .houseNumber(expected.getHouseNumber())
                .city(expected.getCityEn())
                .region(expected.getRegionEn())
                .country(expected.getCountryEn())
                .formattedAddress(expected.getFormattedAddressEn())
                .build())
            .build();
        Assertions.assertEquals(expected, mapper.convert(response));
    }
}

package greencity.mapping;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import org.modelmapper.AbstractConverter;

public class AddressLatLngResponseMapper extends AbstractConverter<AddressLatLngResponse, CoordinatesDto> {

    @Override
    protected CoordinatesDto convert(AddressLatLngResponse addressLatLngResponse) {
        return CoordinatesDto
            .builder()
            .latitude(addressLatLngResponse.getLatitude())
            .longitude(addressLatLngResponse.getLongitude())
            .streetEn(addressLatLngResponse.getAddressEn().getStreet())
            .streetUa(addressLatLngResponse.getAddressUa().getStreet())
            .houseNumber(addressLatLngResponse.getAddressUa().getHouseNumber())
            .cityEn(addressLatLngResponse.getAddressEn().getCity())
            .cityUa(addressLatLngResponse.getAddressUa().getCity())
            .regionEn(addressLatLngResponse.getAddressEn().getRegion())
            .regionUa(addressLatLngResponse.getAddressUa().getRegion())
            .countryEn(addressLatLngResponse.getAddressEn().getCountry())
            .countryUa(addressLatLngResponse.getAddressUa().getCountry())
            .build();
    }
}

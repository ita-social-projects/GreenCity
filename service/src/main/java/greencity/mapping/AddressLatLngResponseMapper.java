package greencity.mapping;

import greencity.dto.event.AddressDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddressLatLngResponseMapper extends AbstractConverter<AddressLatLngResponse, AddressDto> {
    @Override
    protected AddressDto convert(AddressLatLngResponse addressLatLngResponse) {
        AddressDto addressDto = AddressDto
            .builder()
            .latitude(addressLatLngResponse.getLatitude())
            .longitude(addressLatLngResponse.getLongitude())
            .build();
        if (addressLatLngResponse.getAddressUa() != null) {
            addressDto.setStreetUa(addressLatLngResponse.getAddressUa().getStreet());
            addressDto.setHouseNumber(addressLatLngResponse.getAddressUa().getHouseNumber());
            addressDto.setCityUa(addressLatLngResponse.getAddressUa().getCity());
            addressDto.setRegionUa(addressLatLngResponse.getAddressUa().getRegion());
            addressDto.setCountryUa(addressLatLngResponse.getAddressUa().getCountry());
            addressDto.setFormattedAddressUa(addressLatLngResponse.getAddressUa().getFormattedAddress());
        }
        if (addressLatLngResponse.getAddressEn() != null) {
            addressDto.setStreetEn(addressLatLngResponse.getAddressEn().getStreet());
            addressDto.setCityEn(addressLatLngResponse.getAddressEn().getCity());
            addressDto.setRegionEn(addressLatLngResponse.getAddressEn().getRegion());
            addressDto.setCountryEn(addressLatLngResponse.getAddressEn().getCountry());
            addressDto.setFormattedAddressEn(addressLatLngResponse.getAddressEn().getFormattedAddress());
        }
        return addressDto;
    }
}

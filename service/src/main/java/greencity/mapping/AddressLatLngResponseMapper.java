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
        if (addressLatLngResponse.getAddressUk() != null) {
            addressDto.setStreetUk(addressLatLngResponse.getAddressUk().getStreet());
            addressDto.setHouseNumber(addressLatLngResponse.getAddressUk().getHouseNumber());
            addressDto.setCityUk(addressLatLngResponse.getAddressUk().getCity());
            addressDto.setRegionUk(addressLatLngResponse.getAddressUk().getRegion());
            addressDto.setCountryUk(addressLatLngResponse.getAddressUk().getCountry());
            addressDto.setFormattedAddressUk(addressLatLngResponse.getAddressUk().getFormattedAddress());
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

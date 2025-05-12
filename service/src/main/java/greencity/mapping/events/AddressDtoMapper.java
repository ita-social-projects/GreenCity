package greencity.mapping.events;

import greencity.dto.event.AddressDto;
import greencity.entity.event.Address;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoMapper extends AbstractConverter<AddressDto, Address> {
    @Override
    protected Address convert(AddressDto addressDto) {
        return Address.builder()
            .latitude(addressDto.getLatitude())
            .longitude(addressDto.getLongitude())
            .streetEn(addressDto.getStreetEn())
            .streetUk(addressDto.getStreetUk())
            .houseNumber(addressDto.getHouseNumber())
            .cityEn(addressDto.getCityEn())
            .cityUk(addressDto.getCityUk())
            .regionEn(addressDto.getRegionEn())
            .regionUk(addressDto.getRegionUk())
            .countryEn(addressDto.getCountryEn())
            .countryUk(addressDto.getCountryUk())
            .formattedAddressEn(addressDto.getFormattedAddressEn())
            .formattedAddressUk(addressDto.getFormattedAddressUk())
            .build();
    }
}

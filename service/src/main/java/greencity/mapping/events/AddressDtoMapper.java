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
            .streetUa(addressDto.getStreetUa())
            .houseNumber(addressDto.getHouseNumber())
            .cityEn(addressDto.getCityEn())
            .cityUa(addressDto.getCityUa())
            .regionEn(addressDto.getRegionEn())
            .regionUa(addressDto.getRegionUa())
            .countryEn(addressDto.getCountryEn())
            .countryUa(addressDto.getCountryUa())
            .formattedAddressEn(addressDto.getFormattedAddressEn())
            .formattedAddressUa(addressDto.getFormattedAddressUa())
            .build();
    }
}

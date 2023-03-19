package greencity.mapping;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import org.modelmapper.AbstractConverter;

public class AddressLatLngResponseMapper extends AbstractConverter<AddressLatLngResponse, CoordinatesDto> {
    @Override
    protected CoordinatesDto convert(AddressLatLngResponse addressLatLngResponse) {
        CoordinatesDto coordinatesDto = CoordinatesDto
                .builder()
                .latitude(addressLatLngResponse.getLatitude())
                .longitude(addressLatLngResponse.getLongitude())
                .build();
        if(addressLatLngResponse.getAddressUa()!=null){
            coordinatesDto.setStreetUa(addressLatLngResponse.getAddressUa().getStreet());
            coordinatesDto.setHouseNumber(addressLatLngResponse.getAddressUa().getHouseNumber());
            coordinatesDto.setCityUa(addressLatLngResponse.getAddressUa().getCity());
            coordinatesDto.setRegionUa(addressLatLngResponse.getAddressUa().getRegion());
            coordinatesDto.setCountryUa(addressLatLngResponse.getAddressUa().getCountry());
        }
        if(addressLatLngResponse.getAddressEn()!=null){
            coordinatesDto.setStreetEn(addressLatLngResponse.getAddressEn().getStreet());
            coordinatesDto.setCityEn(addressLatLngResponse.getAddressEn().getCity());
            coordinatesDto.setRegionEn(addressLatLngResponse.getAddressEn().getRegion());
            coordinatesDto.setCountryEn(addressLatLngResponse.getAddressEn().getCountry());
        }
        return coordinatesDto;
    }
}

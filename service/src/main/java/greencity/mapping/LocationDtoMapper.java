package greencity.mapping;

import greencity.dto.location.LocationDto;
import greencity.entity.Location;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class LocationDtoMapper extends AbstractConverter<Location, LocationDto> {
    @Override
    protected LocationDto convert(Location location) {
        if (location == null) {
            return null;
        }
        return LocationDto.builder()
                .address(location.getAddressEn())
                .id(location.getId())
                .lat(location.getLat())
                .lng(location.getLng())
                .build();
    }
}

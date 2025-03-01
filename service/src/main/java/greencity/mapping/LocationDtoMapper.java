package greencity.mapping;

import greencity.dto.location.LocationDto;
import greencity.entity.Location;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Location}
 * into {@link LocationDto}.
 */
@Component
public class LocationDtoMapper extends AbstractConverter<Location, LocationDto> {
    /**
     * Method for converting {@link Location} into {@link LocationDto}.
     *
     * @param location object to convert.
     * @return converted object.
     */
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

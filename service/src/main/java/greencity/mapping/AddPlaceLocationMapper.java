package greencity.mapping;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import greencity.dto.location.AddPlaceLocation;
import greencity.entity.Location;

@Component
public class AddPlaceLocationMapper extends AbstractConverter<AddPlaceLocation, Location> {
    @Override
    protected Location convert(AddPlaceLocation source) {
        return Location.builder()
            .addressUk(source.getAddressUk())
            .addressEn(source.getAddressEn())
            .lng(source.getLng())
            .lat(source.getLat())
            .build();
    }
}

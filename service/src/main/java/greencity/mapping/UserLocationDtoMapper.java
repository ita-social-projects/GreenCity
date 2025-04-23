package greencity.mapping;

import greencity.dto.location.UserLocationDto;
import greencity.entity.UserLocation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserLocationDtoMapper extends AbstractConverter<UserLocation, UserLocationDto> {
    @Override
    protected UserLocationDto convert(UserLocation userLocation) {
        return Optional.ofNullable(userLocation)
            .map(ul -> UserLocationDto.builder()
                .id(ul.getId())
                .cityEn(ul.getCityEn())
                .cityUk(ul.getCityUk())
                .regionEn(ul.getRegionEn())
                .regionUk(ul.getRegionUk())
                .countryEn(ul.getCountryEn())
                .countryUk(ul.getCountryUk())
                .latitude(ul.getLatitude())
                .longitude(ul.getLongitude())
                .build())
            .orElse(null);
    }
}

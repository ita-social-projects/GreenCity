package greencity.mapping;

import greencity.dto.user.UserCityDto;
import greencity.entity.UserLocation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class UserCityDtoMapper extends AbstractConverter<UserLocation, UserCityDto> {
    @Override
    protected UserCityDto convert(UserLocation userLocation) {
        return Optional.ofNullable(userLocation)
            .map(ul -> UserCityDto.builder()
                .id(ul.getId())
                .cityEn(ul.getCityEn())
                .cityUk(ul.getCityUk())
                .latitude(ul.getLatitude())
                .longitude(ul.getLongitude())
                .build())
            .orElse(null);
    }
}

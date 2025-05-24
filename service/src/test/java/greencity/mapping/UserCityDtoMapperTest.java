package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserCityDto;
import greencity.entity.UserLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserCityDtoMapperTest {

    @InjectMocks
    UserCityDtoMapper userCityDtoMapper;

    @Test
    void convertTest() {
        UserLocation userLocation = UserLocation.builder()
                .id(1L)
                .cityEn("cityEn").cityUk("cityUk")
                .regionEn("regionEn").regionUk("regionEn")
                .countryEn("countryEn").countryUk("countryUk")
                .latitude(1.).longitude(2.)
                .users(List.of(ModelUtils.getUser()))
                .build();
        UserCityDto expectedResult = UserCityDto.builder()
                .id(userLocation.getId())
                .cityEn(userLocation.getCityEn())
                .cityUk(userLocation.getCityUk())
                .latitude(userLocation.getLatitude())
                .longitude(userLocation.getLongitude())
                .build();

        UserCityDto actualResult = userCityDtoMapper.convert(userLocation);

        assertEquals(expectedResult, actualResult);
    }
}

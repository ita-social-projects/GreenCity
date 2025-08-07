package greencity.mapping;

import static org.junit.jupiter.api.Assertions.*;
import greencity.ModelUtils;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.UserLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @InjectMocks
    private UserMapper userMapper;
    @Mock
    ModelMapper modelMapper;

    @Test
    void convert() {
        UserVO userVO = ModelUtils.getUserVO();

        UserLocation expectedLocation = UserLocation.builder()
            .latitude(userVO.getUserLocation().getLatitude())
            .longitude(userVO.getUserLocation().getLongitude())
            .build();

        User expected = User.builder()
            .id(userVO.getId())
            .name(userVO.getName())
            .userCredo(userVO.getUserCredo())
            .profilePicturePath(userVO.getProfilePicturePath())
            .userLocation(expectedLocation)
            .build();

        Mockito.when(modelMapper.map(userVO.getUserLocation(), UserLocation.class)).thenReturn(expectedLocation);

        assertEquals(expected, userMapper.convert(userVO));
    }
}
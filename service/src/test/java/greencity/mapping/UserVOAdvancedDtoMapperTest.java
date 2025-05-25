package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.dto.location.UserLocationDto;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.entity.User;
import greencity.entity.UserLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserVOAdvancedDtoMapperTest {
    @Mock
    private UserRemoteClient userRemoteClient;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private UserVOAdvancedDtoMapper mapper;

    @Test
    void convertTest() {
        UserVOAdvancedDto expected = ModelUtils.getUserVOAdvancedDto();
        UserVOAdvancedDto toConvert = ModelUtils.getUserVOAdvancedDtoToConvert();
        UserLocation userLocation = ModelUtils.getUserLocation();
        User userToConvert = User.builder()
            .id(1L)
            .userAchievements(List.of(ModelUtils.getUserAchievement()))
            .userFriends(ModelUtils.getUserFriends().stream()
                .map(u -> User.builder()
                    .id(u.getId())
                    .name(u.getName())
                    .build())
                .toList())
            .rating(10.0)
            .userLocation(userLocation)
            .userCredo(expected.getUserCredo())
            .build();

        when(userRemoteClient.findNotDeactivatedByIdAdvanced(userToConvert.getId()))
            .thenReturn(Optional.of(toConvert));
        when(modelMapper.map(userLocation, UserLocationDto.class))
                .thenReturn(ModelUtils.getUserLocationDto());

        assertEquals(expected, mapper.convert(userToConvert));
    }
}

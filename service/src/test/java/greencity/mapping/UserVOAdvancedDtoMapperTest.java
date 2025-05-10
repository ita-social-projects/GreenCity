package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.dto.user.UserVOAdvancedDto;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserVOAdvancedDtoMapperTest {
    @Mock
    private UserRemoteClient userRemoteClient;

    @InjectMocks
    private UserVOAdvancedDtoMapper mapper;

    @Test
    void convertTest() {
        UserVOAdvancedDto expected = ModelUtils.getUserVOAdvancedDto();
        UserVOAdvancedDto toConvert = ModelUtils.getUserVOAdvancedDtoToConvert();
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
            .userCredo(expected.getUserCredo())
            .build();

        when(userRemoteClient.findNotDeactivatedByIdAdvanced(userToConvert.getId()))
            .thenReturn(Optional.of(toConvert));

        assertEquals(expected, mapper.convert(userToConvert));
    }
}

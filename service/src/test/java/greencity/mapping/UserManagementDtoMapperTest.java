package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.dto.user.UserManagementDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementDtoMapperTest {
    @Mock
    UserRemoteClient userRemoteClient;

    @InjectMocks
    UserManagementDtoMapper userManagementDtoMapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();

        UserManagementDto expected = UserManagementDto.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .userCredo(user.getUserCredo())
            .role(userVO.getRole())
            .status(user.getStatus())
            .build();

        when(userRemoteClient.findByEmail(user.getEmail())).thenReturn(Optional.of(userVO));

        assertEquals(expected, userManagementDtoMapper.convert(user));
    }

}

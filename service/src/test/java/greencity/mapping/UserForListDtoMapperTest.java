package greencity.mapping;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserForListDtoMapperTest {
    @Mock
    UserRemoteClient userRemoteClient;

    @InjectMocks
    UserForListDtoMapper userForListDtoMapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();

        UserForListDto expected = UserForListDto.builder()
                .id(userVO.getId())
                .name(userVO.getName())
                .dateOfRegistration(userVO.getDateOfRegistration())
                .email(userVO.getEmail())
                .userStatus(userVO.getUserStatus())
                .role(userVO.getRole())
                .userCredo(userVO.getUserCredo())
                .build();

        when(userRemoteClient.findNotDeactivatedById(user.getId())).thenReturn(Optional.of(userVO));

        assertEquals(expected, userForListDtoMapper.convert(user));
    }
}

package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserProfilePictureDtoMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserProfilePictureDtoMapper mapper;

    @Test
    void convertTest() {
        User user = ModelUtils.getUser();
        UserVO userVO = mock(UserVO.class);
        UserProfilePictureDto expected = UserProfilePictureDto.builder()
            .id(user.getId())
            .name(user.getName())
            .profilePicturePath(user.getProfilePicturePath())
            .build();

        when(modelMapper.map(user, UserVO.class))
            .thenReturn(userVO);
        when(userVO.getProfilePicturePath())
            .thenReturn(expected.getProfilePicturePath());

        assertEquals(expected, mapper.convert(user));
    }
}

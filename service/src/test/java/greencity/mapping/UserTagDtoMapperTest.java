package greencity.mapping;

import greencity.dto.user.UserTagDto;
import greencity.dto.user.UserVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static greencity.ModelUtils.getTagUser;
import static greencity.ModelUtils.getUserTagDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserTagDtoMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserTagDtoMapper mapper;

    @Test
    void convertTest() {
        var user = getTagUser();
        var expected = getUserTagDto();
        UserVO userVO = mock(UserVO.class);

        when(modelMapper.map(user, UserVO.class))
            .thenReturn(userVO);
        when(userVO.getProfilePicturePath())
            .thenReturn(expected.getProfilePicture());

        UserTagDto actual = mapper.convert(user);
        assertEquals(expected, actual);
    }
}

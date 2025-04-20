package greencity.mapping;

import greencity.dto.user.UserManagementVO;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.List;

import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserManagementVO;
import static greencity.ModelUtils.getUserManagementVOPage;
import static greencity.ModelUtils.getUserPage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserManagementVOMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    UserManagementVOMapper userManagementVOMapper;

    @Test
    void convertTest() {
        var user = getUser();
        var userManagementVO = getUserManagementVO();
        UserVO userVO = mock(UserVO.class);

        when(modelMapper.map(user, UserVO.class))
            .thenReturn(userVO);
        when(userVO.getUserCredo())
            .thenReturn(userManagementVO.getUserCredo());
        when(userVO.getRole())
            .thenReturn(userManagementVO.getRole());
        when(userVO.getUserStatus())
            .thenReturn(userManagementVO.getUserStatus());

        UserManagementVO result = userManagementVOMapper.convert(user);
        assertEquals(userManagementVO, result);
    }

    @Test
    void mapAllToPageTest() {
        Page<User> userPage = getUserPage();
        Page<UserManagementVO> expected = getUserManagementVOPage();
        Page<UserVO> userVOPage = new PageImpl<>(List.of(Mockito.mock(UserVO.class)));

        for (int i = 0; i < userPage.getContent().size(); i++) {
            User user = userPage.getContent().get(i);
            UserVO userVO = userVOPage.getContent().get(i);
            UserManagementVO userManagementVO = expected.getContent().get(i);

            when(modelMapper.map(user, UserVO.class))
                .thenReturn(userVO);
            when(userVO.getUserCredo())
                .thenReturn(userManagementVO.getUserCredo());
            when(userVO.getRole())
                .thenReturn(userManagementVO.getRole());
            when(userVO.getUserStatus())
                .thenReturn(userManagementVO.getUserStatus());
        }

        Page<UserManagementVO> result = userManagementVOMapper.mapAllToPage(userPage);

        assertEquals(expected.getContent(), result.getContent());
        assertEquals(expected.getTotalElements(), result.getTotalElements());
        assertEquals(expected.getPageable(), result.getPageable());
    }
}

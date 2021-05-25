package greencity.security.eventlisteners;

import greencity.ModelUtils;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.security.events.SignInEvent;
import greencity.service.HabitAssignService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignInEventListenerTest {

    @Mock
    HabitAssignService habitAssignService;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    private SignInEventListener signInEventListener;

    @Test
    void testOnApplicationEvent() {
        User user = ModelUtils.getUser();
        UserVO userVO = ModelUtils.getUserVO();
        SignInEvent event = new SignInEvent(user);

        User user1 = event.getUser();
        when(modelMapper.map(event.getUser(), UserVO.class)).thenReturn(userVO);
        doNothing().when(habitAssignService).addDefaultHabit(userVO, "en");
        signInEventListener.onApplicationEvent(event);
        verify(habitAssignService).addDefaultHabit(userVO, "en");
    }
}

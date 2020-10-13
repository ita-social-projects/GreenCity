package greencity.security.eventlisteners;

import greencity.ModelUtils;
import greencity.entity.User;
import greencity.security.events.SignInEvent;
import greencity.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SignInEventListenerTest {

    @Mock
    UserService userService;

    @InjectMocks
    private SignInEventListener signInEventListener;

    @Test
    void testOnApplicationEvent() {
        User user = ModelUtils.getUser();
        SignInEvent event = new SignInEvent(user);
        doNothing().when(userService).addDefaultHabit(event.getUser(), "en");
        signInEventListener.onApplicationEvent(event);
        verify(userService).addDefaultHabit(event.getUser(), "en");
    }
}



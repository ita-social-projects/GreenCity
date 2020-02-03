package greencity.security.eventlisteners;

import greencity.entity.User;
import greencity.security.events.SignInEvent;
import greencity.service.UserService;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class SignInEventListenerTest {

    @Mock
    UserService userService;

    private SignInEventListener signInEventListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        signInEventListener = new SignInEventListener(userService);
    }

    @Test
    public void onApplicationEvent() {
        doNothing().when(userService).addDefaultHabit(any(User.class), anyString());
        signInEventListener.onApplicationEvent(new SignInEvent(new User()));

        verify(userService, times(1)).addDefaultHabit(any(User.class), anyString());
    }

    @Test(expected = ClassCastException.class)
    public void onApplicationEventWithInvalidEvent() {
        signInEventListener.onApplicationEvent(new SignInEvent("I'm a User"));
    }
}
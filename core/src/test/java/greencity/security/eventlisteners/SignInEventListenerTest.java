package greencity.security.eventlisteners;

import greencity.entity.User;
import greencity.security.events.SignInEvent;
import greencity.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

class SignInEventListenerTest {

    @Mock
    UserService userService;

    private SignInEventListener signInEventListener;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        signInEventListener = new SignInEventListener(userService);
    }

    @Test
    void onApplicationEvent() {
        doNothing().when(userService).addDefaultHabit(anyLong(), anyString());
        signInEventListener.onApplicationEvent(new SignInEvent(User.builder().id(1L).build()));

        verify(userService, times(1)).addDefaultHabit(anyLong(), anyString());
    }

    @Test
    void onApplicationEventWithInvalidEvent() {
        SignInEvent signInEvent = new SignInEvent("I'm a User");
        Assertions
            .assertThrows(ClassCastException.class,
                () -> signInEventListener.onApplicationEvent(signInEvent));
    }
}
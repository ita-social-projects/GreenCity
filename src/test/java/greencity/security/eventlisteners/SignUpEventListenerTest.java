package greencity.security.eventlisteners;

import greencity.entity.User;
import greencity.security.events.SignUpEvent;
import greencity.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class SignUpEventListenerTest {

    @Mock
    EmailService emailService;

    private SignUpEventListener signUpEventListener;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        signUpEventListener = new SignUpEventListener(emailService);
    }

    @Test
    public void onApplicationEvent() {
        doNothing().when(emailService).sendVerificationEmail(any(User.class));
        signUpEventListener.onApplicationEvent(new SignUpEvent(new User()));

        verify(emailService, times(1)).sendVerificationEmail(any(User.class));
    }

    @Test(expected = ClassCastException.class)
    public void onApplicationEventWithInvalidEvent() {
        signUpEventListener.onApplicationEvent(new SignUpEvent("I'm a User"));
    }
}
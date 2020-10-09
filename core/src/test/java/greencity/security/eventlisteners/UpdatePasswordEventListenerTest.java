package greencity.security.eventlisteners;

import greencity.entity.User;
import greencity.security.events.UpdatePasswordEvent;
import greencity.security.service.OwnSecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePasswordEventListenerTest {
    @Mock
    private OwnSecurityService ownSecurityService;
    @InjectMocks
    private UpdatePasswordEventListener updatePasswordEventListener;

    @Test
    void onApplicationEventTest() {
        UpdatePasswordEvent event = new
            UpdatePasswordEvent(User.builder().id(1L).build(), "Qwerty123=", 1L);
        updatePasswordEventListener.onApplicationEvent(event);
        verify(ownSecurityService).updatePassword(event.getNewPassword(), event.getUserId());
    }
}

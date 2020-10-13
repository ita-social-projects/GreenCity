package greencity.security.eventlisteners;

import greencity.security.events.SignInEvent;
import greencity.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listener for {@link SignInEvent}s.
 *
 * @author Yurii Koval
 */
@Component
@AllArgsConstructor
public class SignInEventListener implements ApplicationListener<SignInEvent> {
    private final UserService userService;

    /**
     * Handles {@link SignInEvent}.
     *
     * @param event {@link SignInEvent}
     */
    @Override
    public void onApplicationEvent(SignInEvent event) {
        userService.addDefaultHabit(event.getUser(), "en");
    }
}

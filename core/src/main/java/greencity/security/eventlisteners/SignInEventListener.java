package greencity.security.eventlisteners;

import greencity.security.events.SignInEvent;
import greencity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listener for {@link SignInEvent}s.
 *
 * @author Yurii Koval
 */
@Component
public class SignInEventListener implements ApplicationListener<SignInEvent> {
    private final UserService userService;

    /**
     * Constructor.
     *
     * @param userService {@link UserService}
     */
    @Autowired
    public SignInEventListener(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles {@link SignInEvent}.
     *
     * @param event {@link SignInEvent}
     */
    @Override
    public void onApplicationEvent(SignInEvent event) {
        userService.addDefaultHabit(event.getUser().getId(), "en");
    }
}

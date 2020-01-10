package greencity.security.eventlisteners;

import greencity.security.events.SignInEvent;
import greencity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SignInEventListener implements ApplicationListener<SignInEvent> {
    private final UserService userService;

    @Autowired
    public SignInEventListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(SignInEvent event) {
        userService.addDefaultHabit(event.getUser(), "en");
    }
}

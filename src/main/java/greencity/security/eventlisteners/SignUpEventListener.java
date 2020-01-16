package greencity.security.eventlisteners;

import greencity.security.events.SignUpEvent;
import greencity.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listener for {@link SignUpEvent}s.
 * @author Yurii Koval.
 */
@Component
public class SignUpEventListener implements ApplicationListener<SignUpEvent> {
    private final EmailService emailService;

    /**
     * Constructor.
     *
     * @param emailService {@link EmailService}
     */
    @Autowired
    public SignUpEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles {@link SignUpEvent}.
     *
     * @param event {@link SignUpEvent} sign up event hat
     */
    @Override
    public void onApplicationEvent(SignUpEvent event) {
        emailService.sendVerificationEmail(event.getUser());
    }
}

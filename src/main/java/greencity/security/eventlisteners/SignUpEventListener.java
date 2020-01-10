package greencity.security.eventlisteners;

import greencity.security.events.SignUpEvent;
import greencity.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SignUpEventListener implements ApplicationListener<SignUpEvent> {
    private final EmailService emailService;

    @Autowired
    public SignUpEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(SignUpEvent event) {
        emailService.sendVerificationEmail(event.getUser());
    }
}

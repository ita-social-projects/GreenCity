package greencity.security.eventlisteners;

import greencity.security.events.SendRestorePasswordEmailEvent;
import greencity.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendRestorePasswordEmailEventListener implements ApplicationListener<SendRestorePasswordEmailEvent> {
    private final EmailService emailService;

    public SendRestorePasswordEmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void onApplicationEvent(SendRestorePasswordEmailEvent event) {
        emailService.sendRestoreEmail(event.getUser(), event.getToken());
    }
}

package greencity.security.eventlisteners;

import greencity.security.events.SendRestorePasswordEmailEvent;
import greencity.service.EmailService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listener that listens to {@link SendRestorePasswordEmailEvent} and delegate
 * email sending logic to {@link EmailService}.
 */
@Component
public class SendRestorePasswordEmailEventListener implements ApplicationListener<SendRestorePasswordEmailEvent> {
    private final EmailService emailService;

    /**
     * Constructor that is used for {@link EmailService} injection, which
     * is responsible for recovery email sending.
     *
     * @param emailService service that directly does sending mail logic
     */
    public SendRestorePasswordEmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Sends password recovery mail to the {@link greencity.entity.User} using
     * generated token.
     *
     * @param event event object from which destination user and recovery token are acquired
     */
    @Override
    public void onApplicationEvent(SendRestorePasswordEmailEvent event) {
        emailService.sendRestoreEmail(event.getUser(), event.getToken());
    }
}

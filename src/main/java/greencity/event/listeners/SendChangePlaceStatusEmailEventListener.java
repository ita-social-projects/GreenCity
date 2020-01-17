package greencity.event.listeners;

import greencity.event.SendChangePlaceStatusEmailEvent;
import greencity.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendChangePlaceStatusEmailEventListener implements ApplicationListener<SendChangePlaceStatusEmailEvent> {
    private final EmailService emailService;

    /**
     * All args constructor.
     */
    @Autowired
    public SendChangePlaceStatusEmailEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Method, that listens to {@link SendChangePlaceStatusEmailEvent} and after triggering sends simple notification
     * for user.
     *
     * @param event {@link SendChangePlaceStatusEmailEvent} with body, that contains data.
     */
    @Override
    public void onApplicationEvent(SendChangePlaceStatusEmailEvent event) {
        emailService.sendChangePlaceStatusEmail(event.getPlace());
    }
}

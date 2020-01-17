package greencity.event.listeners;

import greencity.entity.User;
import greencity.event.SendImmediatelyReportEvent;
import greencity.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendImmediatelyReportEventListener implements ApplicationListener<SendImmediatelyReportEvent> {
    private final EmailService emailService;

    /**
     * All args constructor.
     */
    @Autowired
    public SendImmediatelyReportEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Method, that listens to {@link SendImmediatelyReportEvent} and after triggering sends notification
     * to {@link User}'s who subscribed for updates about added new places.
     *
     * @param event {@link SendImmediatelyReportEvent} with body, that contains data.
     */
    @Override
    public void onApplicationEvent(SendImmediatelyReportEvent event) {
        emailService.sendAddedNewPlacesReportEmail(event.getSubscribers(),
            event.getCategoriesWithPlacesMap(),
            event.getEmailNotification());
    }
}

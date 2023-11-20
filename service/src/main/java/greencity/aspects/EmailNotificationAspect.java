package greencity.aspects;

import greencity.annotations.NotificationType;
import greencity.client.RestClient;
import greencity.dto.EmailSendable;
import greencity.dto.event.EventDto;
import greencity.enums.TypeOfEmailNotification;
import greencity.message.AbstractEmailMessage;
import greencity.message.SendEventCreationNotification;
import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@AllArgsConstructor
public class EmailNotificationAspect {

    private final RestClient restClient;

    @AfterReturning(
            value = "@annotation(typeOfNotification)",
            returning = "result"
    )
    public void defineEmailNotification(NotificationType typeOfNotification, EmailSendable result) {
        TypeOfEmailNotification notificationType = typeOfNotification.type();
        switch (notificationType) {
            case EVENT_CREATED:
                EventDto castedResult = (EventDto) result;
                AbstractEmailMessage emailMessage = SendEventCreationNotification.builder()
                        .email(castedResult.getOrganizer().getEmail())
                        .name(castedResult.getOrganizer().getName())
                        .subject("Event created")
                        .message("You successfully crated an event: " + castedResult.getTitle())
                        .build();
                restClient.sendEmailNotification(emailMessage);
                break;
            default:
                System.out.println("Unknown user action");
        }
    }

}

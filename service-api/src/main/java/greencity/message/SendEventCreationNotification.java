package greencity.message;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * Message, that is used for sending notification about event creation status on
 * user email.
 */
@Getter
@ToString
@NoArgsConstructor
public class SendEventCreationNotification extends AbstractEmailMessage {

    @Builder
    public SendEventCreationNotification(String email, String name, String subject, String message) {
        super(email, name, subject, message);
    }

}

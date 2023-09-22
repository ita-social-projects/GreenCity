package greencity.message;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Message, that is used for sending notification about event creation status on
 * user email.
 */
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendEventCreationNotification implements Serializable {
    private String email;
    private String messageBody;
}

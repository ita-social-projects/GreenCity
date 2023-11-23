package greencity.message;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class EventCommentedMessage extends AbstractEmailMessage{
    @Builder
    public EventCommentedMessage(String email, String name, String subject, String message) {
        super(email, name, subject, message);
    }
}

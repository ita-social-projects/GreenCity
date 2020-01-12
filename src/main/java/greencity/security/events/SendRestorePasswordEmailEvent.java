package greencity.security.events;

import greencity.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendRestorePasswordEmailEvent extends ApplicationEvent {
    private final String token;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public SendRestorePasswordEmailEvent(Object source, String token) {
        super(source);
        this.token = token;
    }

    public User getUser() {
        return (User) getSource();
    }
}

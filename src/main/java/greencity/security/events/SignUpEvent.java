package greencity.security.events;

import greencity.entity.User;
import org.springframework.context.ApplicationEvent;

public class SignUpEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public SignUpEvent(Object source) {
        super(source);
    }

    public User getUser() {
        return (User) source;
    }
}

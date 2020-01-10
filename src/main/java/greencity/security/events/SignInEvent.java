package greencity.security.events;

import greencity.entity.User;
import org.springframework.context.ApplicationEvent;

public class SignInEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public SignInEvent(Object source) {
        super(source);
    }

    public User getUser() {
        return (User) source;
    }
}

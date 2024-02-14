package greencity.security.events;

import greencity.entity.User;
import org.springframework.context.ApplicationEvent;

/**
 * An event that should be emitted when a user signed up.
 *
 * @author Yurii Koval
 */
public class SignUpEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never
     *               {@code null})
     */
    public SignUpEvent(Object source) {
        super(source);
    }

    /**
     * Casts event source to User entity.
     *
     * @return {@link User}
     */
    public User getUser() {
        return (User) source;
    }
}

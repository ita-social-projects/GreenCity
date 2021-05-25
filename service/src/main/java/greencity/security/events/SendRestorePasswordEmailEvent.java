package greencity.security.events;

import greencity.entity.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is meant for notifying about sending password recovery to the
 * email.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class SendRestorePasswordEmailEvent extends ApplicationEvent {
    /**
     * Token which will be sent to user's email and will be used in password
     * recovery process.
     */
    private final String token;

    /**
     * Creates a new {@link SendRestorePasswordEmailEvent} with {@link User} and
     * {@link String} token which will be sent to the user's email.
     *
     * @param source the User whose password is to be recovered
     */
    public SendRestorePasswordEmailEvent(Object source, String token) {
        super(source);
        this.token = token;
    }

    /**
     * Returns {@link User} whose password is to be recovered.
     *
     * @return {@link User} whose password is to be recovered
     */
    public User getUser() {
        return (User) getSource();
    }
}

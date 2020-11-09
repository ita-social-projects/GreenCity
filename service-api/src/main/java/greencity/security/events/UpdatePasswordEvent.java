package greencity.security.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event that is meant for notifying about password update.
 */
@Getter
public class UpdatePasswordEvent extends ApplicationEvent {
    /**
     * New password to which current user's password will be replaced.
     */
    private final String newPassword;
    /**
     * {@link greencity.dto.user.UserVO} id whose password will be updated.
     */
    private final Long userId;

    /**
     * Creates a new {@link UpdatePasswordEvent} with new password and user id,
     * using which this new password will be updated.
     *
     * @param source      the object on which the event initially occurred (never
     *                    {@code null})
     * @param newPassword new password to which current password will be replaced
     * @param userId      user id whose password will be updated
     */
    public UpdatePasswordEvent(Object source, String newPassword, Long userId) {
        super(source);
        this.newPassword = newPassword;
        this.userId = userId;
    }
}

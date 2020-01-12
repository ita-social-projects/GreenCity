package greencity.security.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdatePasswordEvent extends ApplicationEvent {
    private final String newPassword;
    private final Long userId;

    public UpdatePasswordEvent(Object source, String newPassword, Long userId) {
        super(source);
        this.newPassword = newPassword;
        this.userId = userId;
    }
}

package greencity.event;

import greencity.annotations.EventPublishing;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Class that is used for event publishing. Adds message field to {@link ApplicationEvent}.
 * If this event is used as {@link EventPublishing} parameter, {@link CustomApplicationEvent#message} type should be
 * the same as {@link EventMessageResponse#message} type.
 * Classes that extends {@link CustomApplicationEvent} must have
 * {@link CustomApplicationEvent#CustomApplicationEvent(Object, Object)} constructor, in another case event object will
 * fail while creating.
 *
 * @param <M> type of {@link CustomApplicationEvent#message}.
 */
@Getter
@Setter
public abstract class CustomApplicationEvent<M> extends ApplicationEvent {
    private M message;

    /**
     * All args constructor.
     *
     * @param source  the object on which the event initially occurred (never {@code null}).
     * @param message event additional data.
     */
    public CustomApplicationEvent(Object source, M message) {
        super(source);
        this.message = message;
    }
}

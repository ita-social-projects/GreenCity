package greencity.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * Class that is used for events publishing. Adds body field to
 * {@link ApplicationEvent}. Classes that extends {@link CustomApplicationEvent}
 * must have
 * {@link CustomApplicationEvent#CustomApplicationEvent(Object, Object)} and
 * {@link CustomApplicationEvent#CustomApplicationEvent(Object)} constructors,
 * in another case events object will fail while creating.
 *
 * @param <M> type of {@link CustomApplicationEvent#body}.
 */
@Getter
@Setter
public abstract class CustomApplicationEvent<M> extends ApplicationEvent {
    private transient M body;

    /**
     * All args constructor.
     *
     * @param source the object on which the events initially occurred (never
     *               {@code null}).
     * @param body   events additional data.
     */
    protected CustomApplicationEvent(Object source, M body) {
        super(source);
        this.body = body;
    }

    /**
     * One argument constructor.
     *
     * @param source the object on which the events initially occurred (never
     *               {@code null}).
     */
    protected CustomApplicationEvent(Object source) {
        super(source);
    }
}

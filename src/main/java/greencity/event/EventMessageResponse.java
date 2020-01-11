package greencity.event;

import greencity.annotations.EventPublishing;
import lombok.Data;

/**
 * Class that is used as return type of methods, that annotated with {@link EventPublishing}.
 * {@link EventMessageResponse#message} is used as event data. If this field is {@code null}, then event will not be
 * published.
 * {@link EventMessageResponse#source} is used as event source.
 * {@link EventMessageResponse#response} is used as service method returning.
 *
 * @param <M> event message type.
 * @param <R> response type.
 */
@Data
public class EventMessageResponse<M, R> {
    private M message;
    private R response;
    private Object source;

    /**
     * All args constructor. Used when execution should trigger event.
     */
    public EventMessageResponse(M message, R response, Object source) {
        this.message = message;
        this.response = response;
        this.source = source;
    }

    /**
     * Used when execution should not trigger event.
     */
    public EventMessageResponse(R response) {
        this.response = response;
    }
}

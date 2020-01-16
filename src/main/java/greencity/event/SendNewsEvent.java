package greencity.event;

import greencity.dto.econews.AddEcoNewsDtoResponse;

/**
 * Event, that triggers news sending by mail. Contains needed data for mail sending.
 */
public class SendNewsEvent extends CustomApplicationEvent<AddEcoNewsDtoResponse> {
    /**
     * All args constructor.
     *
     * @param source the object on which the events initially occurred (never {@code null}).
     * @param body   events additional data.
     */
    public SendNewsEvent(Object source, AddEcoNewsDtoResponse body) {
        super(source, body);
    }

    /**
     * One argument constructor.
     *
     * @param source the object on which the events initially occurred (never {@code null}).
     */
    public SendNewsEvent(Object source) {
        super(source);
    }
}

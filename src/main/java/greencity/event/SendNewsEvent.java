package greencity.event;

import greencity.event.messages.SendNewsByEmailMessage;

/**
 * Event, that triggers news sending by mail. Contains needed data for mail sending.
 */
public class SendNewsEvent extends CustomApplicationEvent<SendNewsByEmailMessage> {
    /**
     * All args constructor.
     *
     * @param source  the object on which the event initially occurred (never {@code null}).
     * @param message event additional data.
     */
    public SendNewsEvent(Object source, SendNewsByEmailMessage message) {
        super(source, message);
    }
}

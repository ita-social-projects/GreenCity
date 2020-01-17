package greencity.event;

import greencity.entity.Place;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendChangePlaceStatusEmailEvent extends ApplicationEvent {
    private final Place place;

    /**
     * All args constructor.
     */
    public SendChangePlaceStatusEmailEvent(Object source, Place place) {
        super(source);
        this.place = place;
    }
}

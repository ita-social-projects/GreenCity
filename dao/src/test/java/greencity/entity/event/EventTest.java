package greencity.entity.event;

import greencity.ModelUtils;
import greencity.enums.EventType;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(SpringExtension.class)
public class EventTest {

    @Test
    public void testGetEventTypeOffline() {
        Event event = ModelUtils.getOfflineEvent();
        EventType eventType = event.getEventType();

        assertEquals(EventType.OFFLINE, eventType);
    }

    @Test
    public void testGetEventTypeOnline() {
        Event event = ModelUtils.getOnlineEvent();
        EventType eventType = event.getEventType();

        assertEquals(EventType.ONLINE, eventType);
    }

    @Test
    public void testGetEventTypeOnlineOffline() {
        Event event = ModelUtils.getOnlineOfflineEvent();
        EventType eventType = event.getEventType();

        assertEquals(EventType.ONLINE_OFFLINE, eventType);
    }
}

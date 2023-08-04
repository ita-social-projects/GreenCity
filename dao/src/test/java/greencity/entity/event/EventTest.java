package greencity.entity.event;

import greencity.ModelUtils;
import greencity.enums.EventType;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EventTest {
    @Test
    public void testGetEventTypeOffline() {
        Event eventOffline = ModelUtils.getOfflineEvent();
        List<EventDateLocation> dates = new ArrayList<>();

        EventDateLocation mockDate1 = mock(EventDateLocation.class);
        when(mockDate1.getAddress()).thenReturn(ModelUtils.getAddress());
        dates.add(mockDate1);

        eventOffline.setDates(dates);
        assertEquals(EventType.OFFLINE, eventOffline.getEventType());
    }

    @Test
    public void testGetEventTypeOnline() {
        Event eventOnline = ModelUtils.getOnlineEvent();
        List<EventDateLocation> dates = new ArrayList<>();

        EventDateLocation mockDate2 = mock(EventDateLocation.class);
        when(mockDate2.getOnlineLink()).thenReturn("https://example.com/online-event");
        dates.add(mockDate2);

        eventOnline.setDates(dates);
        assertEquals(EventType.ONLINE, eventOnline.getEventType());
    }

    @Test
    public void testGetEventTypeOnlineOffline() {
        Event eventOnlineOffline = ModelUtils.getOnlineOfflineEvent();
        List<EventDateLocation> dates = new ArrayList<>();

        EventDateLocation mockDate3 = mock(EventDateLocation.class);
        when(mockDate3.getOnlineLink()).thenReturn("https://example.com/online-event");
        when(mockDate3.getAddress()).thenReturn(ModelUtils.getAddress());
        dates.add(mockDate3);

        eventOnlineOffline.setDates(dates);
        assertEquals(EventType.ONLINE_OFFLINE, eventOnlineOffline.getEventType());
    }

    @Test
    public void testIsRelevantTrue() {
        Event event = ModelUtils.getOfflineEvent();
        List<EventDateLocation> dates = new ArrayList<>();

        EventDateLocation mockDate = mock(EventDateLocation.class);
        when(mockDate.getFinishDate()).thenReturn(ZonedDateTime.now().plusDays(1));
        dates.add(mockDate);

        event.setDates(dates);
        assertTrue(event.isRelevant());
    }

    @Test
    public void testIsRelevantFalse() {
        Event event = ModelUtils.getOfflineEvent();
        List<EventDateLocation> dates = new ArrayList<>();

        EventDateLocation mockDate = mock(EventDateLocation.class);
        when(mockDate.getFinishDate()).thenReturn(ZonedDateTime.now().minusDays(1));
        dates.add(mockDate);

        event.setDates(dates);
        assertFalse(event.isRelevant());
    }
}

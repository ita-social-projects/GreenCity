package greencity.dto.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventViewDtoTest {
    @Test
    void isEmptyTrue() {
        EventViewDto eventViewDto = new EventViewDto("", "", "", "", "");
        assertTrue(eventViewDto.isEmpty());
    }

    @Test
    void isEmptyFalse() {
        EventViewDto eventViewDto =
            new EventViewDto("1L", "", "", "", "");
        assertFalse(eventViewDto.isEmpty());
    }
}

package greencity.dto.event;

import greencity.ModelUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventDtoTest {

    @Test
    void EmptyTagString() {
        assertEquals("", ModelUtils.getEventDtoWithoutTag().tagsToStringEn());
    }

    @Test
    void OneTagString() {
        assertEquals("Social", ModelUtils.getEventDtoWithTag().tagsToStringEn());
    }
}

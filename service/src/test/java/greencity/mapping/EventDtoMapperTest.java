package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.EventDto;
import greencity.entity.event.Event;
import greencity.mapping.events.EventDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class EventDtoMapperTest {
    @InjectMocks
    EventDtoMapper mapper;

    @Test
    void convertTest() {
        Event event = ModelUtils.getEvent();
        event.setAdditionalImages(new ArrayList<>());

        EventDto expected = ModelUtils.getEventDto();

        assertEquals(expected.getTitle(), mapper.convert(event).getTitle());
    }

    @Test
    void convertWithoutAddressTest() {
        Event event = ModelUtils.getEventWithoutAddress();
        event.setAdditionalImages(new ArrayList<>());

        EventDto expected = ModelUtils.getEventWithoutAddressDto();

        assertEquals(expected.getTitle(), mapper.convert(event).getTitle());
    }
}

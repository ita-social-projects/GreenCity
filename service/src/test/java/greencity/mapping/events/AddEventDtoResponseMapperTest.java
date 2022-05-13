package greencity.mapping.events;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoResponse;
import greencity.entity.Event;
import greencity.mapping.events.AddEventDtoResponseMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class AddEventDtoResponseMapperTest {
    @InjectMocks
    AddEventDtoResponseMapper mapper;

    @Test
    void convertTest() {
        Event event = ModelUtils.getEvent();
        event.setAdditionalImages(new ArrayList<>());

        AddEventDtoResponse expected = ModelUtils.getAddEventDtoResponse();

        assertEquals(expected.getDescription(), mapper.convert(event).getDescription());
    }

    @Test
    void convertTestWithoutCoordinates() {
        Event event = ModelUtils.getEventWithoutCoordinates();
        event.setAdditionalImages(new ArrayList<>());

        AddEventDtoResponse expected = ModelUtils.addEventDtoWithoutCoordinatesResponse;

        assertEquals(expected.getDescription(), mapper.convert(event).getDescription());
    }
}

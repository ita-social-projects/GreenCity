package greencity.mapping.events;

import greencity.ModelUtils;
import greencity.dto.event.AddEventDtoRequest;
import greencity.entity.event.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class AddEventDtoRequestMapperTest {
    @Mock
    private CoordinatesDtoMapper coordinatesDtoMapper;
    @InjectMocks
    private AddEventDtoRequestMapper mapper;

    @Test
    void convertTest() {
        Event expected = ModelUtils.getEventWithoutCoordinates();

        AddEventDtoRequest request = ModelUtils.addEventDtoRequest;

        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
    }

    @Test
    void convertTestWithoutCoordinates() {
        Event expected = ModelUtils.getEventWithoutCoordinates();

        AddEventDtoRequest request = ModelUtils.addEventDtoWithoutCoordinatesRequest;

        assertEquals(expected.getTitle(), mapper.convert(request).getTitle());
    }
}

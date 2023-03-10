package greencity.mapping.events;

import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.entity.event.EventDateLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventDateLocationDtoMapperTest {
    @InjectMocks
    private EventDateLocationDtoMapper mapper;

    @Test
    void convert() {
        EventDateLocation expected = getEventDateLocation();
        EventDateLocationDto dto = EventDateLocationDto.builder()
            .id(expected.getId())
            .startDate(expected.getStartDate())
            .finishDate(expected.getFinishDate())
            .coordinates(getCoordinatesDto())
            .build();
        EventDateLocation actual = mapper.convert(dto);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStartDate(), actual.getStartDate());
        assertEquals(expected.getCoordinates(), actual.getCoordinates());
    }
}

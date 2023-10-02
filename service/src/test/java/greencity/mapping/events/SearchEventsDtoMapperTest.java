package greencity.mapping.events;

import greencity.ModelUtils;
import greencity.dto.search.SearchEventsDto;
import greencity.entity.event.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class SearchEventsDtoMapperTest {

    @InjectMocks
    SearchEventsDtoMapper searchEventsDtoMapper;

    @Test
    void convertTest() {
        Event event = ModelUtils.getEvent();
        SearchEventsDto expected = ModelUtils.getSearchEvents();
        assertEquals(expected.getTitle(), searchEventsDtoMapper.convert(event).getTitle());
    }
}

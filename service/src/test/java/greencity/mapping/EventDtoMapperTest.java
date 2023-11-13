package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.EventDto;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.CommentStatus;
import greencity.mapping.events.EventDtoMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class EventDtoMapperTest {
    @InjectMocks
    EventDtoMapper mapper;

    @Test
    void convertTest() {
        Event event = ModelUtils.getEvent();
        event.setAdditionalImages(new ArrayList<>());
        event.setUsersLikedEvents(Set.of(ModelUtils.getUser()));
        var eventComment = ModelUtils.getEventComment();
        var eventComment2 = ModelUtils.getEventComment();
        eventComment2.setStatus(CommentStatus.DELETED);
        event.setEventsComments(Arrays.asList(eventComment, eventComment2));
        EventDto expected = ModelUtils.getEventDto();

        EventDto result = mapper.convert(event);

        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(event.getUsersLikedEvents().size(), result.getLikes());
        assertEquals(
            event.getEventsComments().stream().filter(deleted -> deleted.getStatus().equals(CommentStatus.DELETED))
                .count(),
            result.getCountComments());
    }

    @Test
    void convertWithoutAddressTest() {
        Event event = ModelUtils.getEventWithoutAddress();
        event.setAdditionalImages(new ArrayList<>());

        EventDto expected = ModelUtils.getEventWithoutAddressDto();

        assertEquals(expected.getTitle(), mapper.convert(event).getTitle());
    }
}

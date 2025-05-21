package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.event.EventDto;
import greencity.dto.user.UserVO;
import greencity.entity.event.Event;
import greencity.mapping.events.EventDtoMapper;
import greencity.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventDtoMapperTest {
    @InjectMocks
    EventDtoMapper mapper;
    @Mock
    CommentService commentService;

    @Mock
    ModelMapper modelMapper;

    @Test
    void convertTest() {
        Event event = ModelUtils.getEvent();
        event.setAdditionalImages(new ArrayList<>());
        event.setUsersLikedEvents(Set.of(ModelUtils.getUser()));
        EventDto expected = ModelUtils.getEventDto();
        UserVO userVO = mock(UserVO.class);
        String email = "email";

        when(modelMapper.map(event.getOrganizer(), UserVO.class))
                .thenReturn(userVO);
        when(userVO.getEmail())
                .thenReturn(email);

        EventDto result = mapper.convert(event);

        assertEquals(expected.getTitle(), result.getTitle());
        assertEquals(event.getUsersLikedEvents().size(), result.getLikes());
    }

    @Test
    void convertWithoutAddressTest() {
        Event event = ModelUtils.getEventWithoutAddress();
        event.setAdditionalImages(new ArrayList<>());
        UserVO userVO = mock(UserVO.class);
        String email = "email";

        when(modelMapper.map(event.getOrganizer(), UserVO.class))
                .thenReturn(userVO);
        when(userVO.getEmail())
                .thenReturn(email);

        EventDto expected = ModelUtils.getEventWithoutAddressDto();

        assertEquals(expected.getTitle(), mapper.convert(event).getTitle());
    }
}

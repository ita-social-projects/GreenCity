package greencity.controller;

import greencity.converters.UserArgumentResolver;
import greencity.service.EventService;
import greencity.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventsControllerTest {

    private static final String EVENTS_CONTROLLER_LINK = "/events";

    private MockMvc mockMvc;

    @InjectMocks
    private EventsController eventsController;

    @Mock
    private EventService eventService;

    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    private final Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(eventsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    @SneakyThrows
    void getEventsCreatedByUser() {
        int pageNumber = 0;
        int pageSize = 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(EVENTS_CONTROLLER_LINK + "/myEvents/createdEvents").principal(principal))
            .andExpect(status().isOk());
        verify(eventService).getEventsCreatedByUser(pageable, "test@gmail.com");

    }

    @Test
    @SneakyThrows
    void getUserEventsTest() {
        int pageNumber = 0;
        int pageSize = 20;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(EVENTS_CONTROLLER_LINK + "/myEvents").principal(principal))
            .andExpect(status().isOk());

        verify(eventService).getAllUserEvents(pageable, "test@gmail.com");
    }

    @Test
    @SneakyThrows
    void addAttenderTest() {
        Long eventId = 1L;

        mockMvc.perform(post(EVENTS_CONTROLLER_LINK + "/addAttender/{eventId}", eventId).principal(principal));

        verify(eventService).addAttender(eventId, "test@gmail.com");
    }

    @Test
    @SneakyThrows
    void removeAttenderTest() {
        Long eventId = 1L;

        mockMvc.perform(delete(EVENTS_CONTROLLER_LINK + "/removeAttender/{eventId}", eventId).principal(principal))
            .andExpect(status().isOk());

        verify(eventService).removeAttender(eventId, "test@gmail.com");
    }

    @Test
    @SneakyThrows
    void rateEventTest() {
        Long eventId = 1L;
        int grade = 2;

        mockMvc
            .perform(post(EVENTS_CONTROLLER_LINK + "/rateEvent/{eventId}/{grade}", eventId, grade).principal(principal))
            .andExpect(status().isOk());

        verify(eventService).rateEvent(eventId, "test@gmail.com", grade);
    }

    @Test
    @SneakyThrows
    void getAllEventSubscribersTest() {
        Long eventId = 1L;

        mockMvc.perform(get(EVENTS_CONTROLLER_LINK + "/getAllSubscribers/{eventId}", eventId))
            .andExpect(status().isOk());

        verify(eventService).getAllEventAttenders(eventId);
    }
}

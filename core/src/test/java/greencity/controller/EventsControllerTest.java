package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.UpdateEventDto;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void saveTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String json = "{\n" +
                "    \"title\":\"string\",\n" +
                "    \"description\":\"stringstringstringstringstringstringstringstring\",\n" +
                "    \"open\":true,\n" +
                "    \"datesLocations\":[\n" +
                "        {\n" +
                "            \"startDate\":\"2023-05-27T15:00:00Z\",\n" +
                "            \"finishDate\":\"2023-05-27T17:00:00Z\",\n" +
                "            \"coordinates\":{\n" +
                "                \"latitude\":1,\n" +
                "                \"longitude\":1\n" +
                "            },\n" +
                "            \"onlineLink\":\"http://localhost:8080/swagger-ui.html#/events-controller\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"tags\":[\"Social\"]\n" +
                "}   ";

        MockMultipartFile jsonFile =
                new MockMultipartFile("addEventDtoRequest", "", "application/json", json.getBytes());

        mockMvc.perform(multipart(EVENTS_CONTROLLER_LINK + "/create")
                .file(jsonFile)
                .principal(principal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        AddEventDtoRequest addEventDtoRequest = objectMapper.readValue(json, AddEventDtoRequest.class);

        verify(eventService).save(addEventDtoRequest, principal.getName(), new MultipartFile[0]);
    }

    @Test
    @SneakyThrows
    void saveBadRequestTest() {
        String json = "{}";
        MockMultipartFile jsonFile =
                new MockMultipartFile("addEventDtoRequest", "", "application/json", json.getBytes());
        mockMvc.perform(multipart(EVENTS_CONTROLLER_LINK + "/create")
                .file(jsonFile)
                .principal(principal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }



    @Test
    @SneakyThrows
    void deleteTest() {

        mockMvc.perform(delete(EVENTS_CONTROLLER_LINK + "/delete/{eventId}", 1)
                .principal(principal))
                .andExpect(status().isOk());

        verify(eventService).delete(1L, "test@gmail.com");
    }

    @Test
    @SneakyThrows
    void deleteFailedTest() {
        mockMvc.perform(delete(EVENTS_CONTROLLER_LINK + "/delete/{eventId}", "not_number")
                .principal(principal))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String json = "{\n" +
                "    \"title\":\"string\",\n" +
                "    \"description\":\"stringstringstringstringstringstringstringstring\",\n" +
                "    \"open\":true,\n" +
                "    \"datesLocations\":[\n" +
                "        {\n" +
                "            \"startDate\":\"2023-05-27T15:00:00Z\",\n" +
                "            \"finishDate\":\"2023-05-27T17:00:00Z\",\n" +
                "            \"coordinates\":{\n" +
                "                \"latitude\":1,\n" +
                "                \"longitude\":1\n" +
                "            },\n" +
                "            \"onlineLink\":\"http://localhost:8080/swagger-ui.html#/events-controller\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"tags\":[\"Social\"]\n" +
                "}   ";

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart(EVENTS_CONTROLLER_LINK + "/update");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        MockMultipartFile jsonFile =
                new MockMultipartFile("eventDto", "", "application/json", json.getBytes());

        mockMvc.perform(builder
                .file(jsonFile)
                .principal(principal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        UpdateEventDto eventDto = objectMapper.readValue(json, UpdateEventDto.class);

        verify(eventService).update(eventDto, principal.getName(), new MultipartFile[0]);
    }

    @Test
    @SneakyThrows
    void updateBadRequestTest() {
        String json = "";

        MockMultipartFile jsonFile =
                new MockMultipartFile("eventDto", "", "application/json", json.getBytes());

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart(EVENTS_CONTROLLER_LINK + "/update");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                .file(jsonFile)
                .principal(principal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getEventTest() {
        mockMvc.perform(get(EVENTS_CONTROLLER_LINK + "/event/{eventId}", 1L).principal(principal))
                .andExpect(status().isOk());

        verify(eventService)
                .getEvent(1L, principal);
    }

    @Test
    @SneakyThrows
    void getEventFailedTest() {
        mockMvc.perform(get(EVENTS_CONTROLLER_LINK + "/event/{eventId}", "not_number").principal(principal))
                .andExpect(status().isBadRequest());

        verify(eventService, times(0))
                .getEvent(1L, principal);
    }

}

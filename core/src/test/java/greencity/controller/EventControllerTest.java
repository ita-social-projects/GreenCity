package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.dto.event.AddEventDtoRequest;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
import greencity.service.UserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventControllerTest {
    private static final String eventsLink = "/events";

    private MockMvc mockMvc;

    @InjectMocks
    private EventsController eventsController;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private EventService eventService;

    private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

    private String createJson =
        "{\n" +
            "  \"title\": \"string\",\n" +
            "  \"description\": \"string\",\n" +
            "  \"coordinates\":  {\"latitude\": 1, \"longitude\" : 2}\n" +
            "}";;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders
            .standaloneSetup(eventsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes))
            .build();
    }

    @Test
    @SneakyThrows
    void saveTest() {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("danylo@gmail.com");

        MockMultipartFile jsonFile =
            new MockMultipartFile("addEventDtoRequest", "", "application/json", createJson.getBytes());

        this.mockMvc.perform(multipart(eventsLink + "/create")
            .file(jsonFile)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddEventDtoRequest addEcoNewsDtoRequest = mapper.readValue(createJson, AddEventDtoRequest.class);

        verify(eventService)
            .save(eq(addEcoNewsDtoRequest), eq("danylo@gmail.com"), any());
    }

    @Test
    @SneakyThrows
    void save413ResponseTest() {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenThrow(new MultipartException(""));

        MockMultipartFile jsonFile =
            new MockMultipartFile("addEventDtoRequest", "", "application/json", createJson.getBytes());
        try {
            this.mockMvc.perform(multipart(eventsLink + "/create")
                .file(jsonFile)
                .principal(principal)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MultipartException));

        } catch (MultipartException e) {
            assertTrue(true);
        }
    }
}

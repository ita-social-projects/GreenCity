package greencity.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;

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

    private String createJson = "{\n" +
        "  \"dates\": [\n" +
        "    {\n" +
        "      \"coordinatesDto\": {\n" +
        "        \"latitude\": 0,\n" +
        "        \"longitude\": 0\n" +
        "      },\n" +
        "      \"startDate\" : \"2016-05-28T17:39:44.937\", \n" +
        "      \"finishDate\" : \"2016-05-28T18:39:44.938\",\n" +
        "      \"onlineLink\": \"string\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"description\": \"string\",\n" +
        "  \"open\": true,\n" +
        "  \"title\": \"string\"\n" +
        "}";

    @BeforeEach
    public void setUp() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter =
            new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(new ObjectMapper().setPropertyNamingStrategy(
            PropertyNamingStrategies.SNAKE_CASE));
        mockMvc = MockMvcBuilders.standaloneSetup(eventsController)
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes))
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .setControllerAdvice(new CustomExceptionHandler(errorAttributes))
            .setMessageConverters(mappingJackson2HttpMessageConverter).build();
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

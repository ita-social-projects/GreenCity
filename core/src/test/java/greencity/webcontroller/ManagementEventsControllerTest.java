package greencity.webcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagDto;
import greencity.enums.TagType;
import greencity.exception.handler.CustomExceptionHandler;
import greencity.service.EventService;
import greencity.service.TagsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementEventsControllerTest {
    private static final String managementEventsLink = "/management/events";
    private static final String managementAddEventLink = "/management/events/create";

    @Mock EventService eventService;
    @InjectMocks ManagementEventsController managementEventsController;
    @Mock private TagsService tagsService;
    @Mock private ErrorAttributes errorAttributes;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementEventsController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setControllerAdvice(new CustomExceptionHandler(errorAttributes, objectMapper))
                .build();
    }

    @Test
    void shouldSaveEventWithValidJson() throws Exception {
        //given
        String content = loadResourceAsString("testdata/event/validCreateEventDto.json");

        Principal principal = Mockito.mock(Principal.class);
        EventDto eventDto = new EventDto();
        eventDto.setOpen(true);
        String title = "test title";
        eventDto.setTitle(title);
        String description = "sample test description";
        eventDto.setDescription(description);

        when(eventService.save(any(), any(), any())).thenReturn(eventDto);

        //expect
        mockMvc.perform(post(managementAddEventLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(principal)
                        .content(content))
                .andExpect(jsonPath("$.title").value(eventDto.getTitle()))
                .andExpect(jsonPath("$.description").value(eventDto.getDescription()))
                .andExpect(jsonPath("$.open").value(eventDto.isOpen()))
                .andExpect(status().isCreated());

        verify(eventService, times(1)).save(any(),any(),any());
    }

    @Test
    void shouldNotSaveEventWithInvalidJson() throws Exception {
        //given
        String content = loadResourceAsString("testdata/event/invalidCreateEventDto.json");

        Principal principal = Mockito.mock(Principal.class);
        EventDto eventDto = new EventDto();
        String title = "sample title";
        eventDto.setTitle(title);
        String description = "test description with description";
        eventDto.setDescription(description);

        when(eventService.save(any(), any(), any())).thenReturn(eventDto);

        //expect
        mockMvc.perform(post(managementAddEventLink)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .principal(principal)
                        .content(content))
                .andExpect(jsonPath("$[*].name").value("description"))
                .andExpect(jsonPath("$[*].message").value("must not be empty"))
                .andExpect(status().is4xxClientError());

        verify(eventService, never()).save(any(), any(), any());
    }

    @Test
    void getAllEventsWithoutQuery() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventDto> eventDtos = Collections.singletonList(new EventDto());
        PageableAdvancedDto<EventDto> eventsDtoPageableDto =
                new PageableAdvancedDto<>(eventDtos, 2, 0, 3, 0, true, true, true, true);
        List<TagDto> tagDtoList = Collections.singletonList(TagDto.builder()
                .id(1L)
                .name("Social").build());
        when(tagsService.findByTypeAndLanguageCode(TagType.EVENT, "en")).thenReturn(tagDtoList);
        when(eventService.getAll(pageable, null)).thenReturn(eventsDtoPageableDto);

        this.mockMvc.perform(get(managementEventsLink)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(view().name("core/management_events"))
                .andExpect(model().attribute("pageable", eventsDtoPageableDto))
                .andExpect(status().isOk());

        verify(eventService).getAll(pageable, null);
    }

    @Test
    @SneakyThrows
    void getAllEventsWithQuery() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventDto> eventDtos = Collections.singletonList(new EventDto());
        PageableAdvancedDto<EventDto> eventsDtoPageableDto =
                new PageableAdvancedDto<>(eventDtos, 2, 0, 3, 0, true, true, true, true);
        List<TagDto> tagDtoList = Collections.singletonList(TagDto.builder()
                .id(1L)
                .name("Social").build());
        when(tagsService.findByTypeAndLanguageCode(TagType.EVENT, "en")).thenReturn(tagDtoList);
        when(eventService.searchEventsBy(pageable, "query")).thenReturn(eventsDtoPageableDto);

        this.mockMvc.perform(get(managementEventsLink)
                        .param("page", "0")
                        .param("size", "10")
                        .param("query", "query"))
                .andExpect(view().name("core/management_events"))
                .andExpect(model().attribute("pageable", eventsDtoPageableDto))
                .andExpect(status().isOk());

        verify(eventService).searchEventsBy(pageable, "query");
    }

    @Test
    @SneakyThrows
    void getAllEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventDto> eventDtos = Collections.singletonList(new EventDto());
        PageableAdvancedDto<EventDto> eventsDtoPageableDto =
                new PageableAdvancedDto<>(eventDtos, 2, 0, 3, 0, true, true, true, true);
        List<TagDto> tagDtoList = Collections.singletonList(TagDto.builder()
                .id(1L)
                .name("Social").build());
        when(tagsService.findByTypeAndLanguageCode(TagType.EVENT, "en")).thenReturn(tagDtoList);
        when(eventService.getAll(pageable, null)).thenReturn(eventsDtoPageableDto);

        this.mockMvc.perform(get(managementEventsLink)
                        .param("page", "0")
                        .param("size", "10")
                        .param("query", "query")
                        .param("title", "title"))
                .andExpect(view().name("core/management_events"))
                .andExpect(model().attribute("pageable", eventsDtoPageableDto))
                .andExpect(status().isOk());

        verify(eventService).getAll(pageable, null);
    }

    private String loadResourceAsString(String name) {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(name);
        if (resourceAsStream == null) {
            throw new RuntimeException("resource " + name + " not found");
        }
        try (Reader reader = new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}

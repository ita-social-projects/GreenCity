package greencity.webcontroller;

import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagDto;
import greencity.enums.TagType;
import greencity.service.EventService;
import greencity.service.TagsService;
import greencity.service.UserService;
import java.util.Collections;
import java.util.List;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagementEventsControllerTest {
    private static final String managementEventsLink = "/management/events";
    @Mock
    EventService eventService;
    @InjectMocks
    ManagementEventsController managementEventsController;
    private MockMvc mockMvc;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TagsService tagsService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(managementEventsController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
    }

    @Test
    @SneakyThrows
    void getAllEventsWithoutQuery() {
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
            .param("size", "10"))
            .andExpect(view().name("core/management_events"))
            .andExpect(model().attribute("pageable", eventsDtoPageableDto))
            .andExpect(status().isOk());

        verify(eventService).getAll(pageable, null);
    }
}

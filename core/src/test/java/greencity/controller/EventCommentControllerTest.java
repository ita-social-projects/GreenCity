package greencity.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.user.UserVO;
import greencity.service.EventCommentService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
public class EventCommentControllerTest {
    private static final String eventCommentControllerLink = "/events/comments";
    private MockMvc mockMvc;
    @InjectMocks
    private EventCommentController eventCommentController;
    @Mock
    private EventCommentService eventCommentService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    private final Principal principal = getPrincipal();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(eventCommentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                        new UserArgumentResolver(userService, modelMapper))
                .build();
    }

    @Test
    void save() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
        String content = "{\n"
                + "  \"text\": \"string\"\n"
                + "}";

        mockMvc.perform(post(eventCommentControllerLink + "/{eventId}", 1)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddEventCommentDtoRequest addEventCommentDtoRequest =
                mapper.readValue(content, AddEventCommentDtoRequest.class);

        verify(userService).findByEmail("test@gmail.com");
        verify(eventCommentService).save(1L, addEventCommentDtoRequest, userVO);
    }

    @Test
    void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(eventCommentControllerLink + "/{eventId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllActiveComments() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(eventCommentControllerLink + "/active?eventId=1&page=5")
                        .principal(principal))
                .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(eventCommentService).getAllActiveComments(pageable, userVO, 1L);
    }

    @Test
    void countComments() throws Exception {
        mockMvc.perform(get(eventCommentControllerLink + "/count/comments/{eventId}", 1))
                .andExpect(status().isOk());

        verify(eventCommentService).countComments(1L);
    }
}

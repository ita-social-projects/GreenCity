package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.user.UserVO;
import greencity.service.EcoNewsCommentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
class EcoNewsCommentControllerTest {
    private static final String ecoNewsCommentControllerLink = "/econews/comments";
    private MockMvc mockMvc;

    @InjectMocks
    private EcoNewsCommentController ecoNewsCommentController;

    @Mock
    private EcoNewsCommentService ecoNewsCommentService;

    @Mock
    private UserService userService;

    @Mock
    private ModelMapper modelMapper;

    private final Principal principal = getPrincipal();

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(ecoNewsCommentController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver(),
                new UserArgumentResolver(userService, modelMapper))
            .build();
        mapper = new ObjectMapper();
    }

    @Test
    void save() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
        String content = "{\n"
            + "  \"parentCommentId\": 0,\n"
            + "  \"text\": \"string\"\n"
            + "}";

        mockMvc.perform(post(ecoNewsCommentControllerLink + "/{econewsId}", 1)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest =
            mapper.readValue(content, AddEcoNewsCommentDtoRequest.class);

        verify(userService).findByEmail("test@gmail.com");
        verify(ecoNewsCommentService).save(1L, addEcoNewsCommentDtoRequest, userVO);
    }

    @Test
    void saveBadRequestTest() throws Exception {
        mockMvc.perform(post(ecoNewsCommentControllerLink + "/{econewsId}", 1)
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
        mockMvc.perform(get(ecoNewsCommentControllerLink + "/active?ecoNewsId=1&page=5")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(ecoNewsCommentService).getAllActiveComments(pageable, userVO, 1L);
    }

    @Test
    void getCountOfComments() throws Exception {
        mockMvc.perform(get(ecoNewsCommentControllerLink + "/count/comments/{ecoNewsId}", 1))
            .andExpect(status().isOk());

        verify(ecoNewsCommentService).countOfComments(1L);
    }

    @Test
    void findAllReplies() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsCommentControllerLink + "/replies/{parentCommentId}?page=5&size=20", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(ecoNewsCommentService).findAllReplies(pageable, 1L, userVO);
    }

    @Test
    void findAllActiveReplies() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        mockMvc.perform(get(ecoNewsCommentControllerLink + "/replies/active/{parentCommentId}?page=5&size=20", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(ecoNewsCommentService).findAllActiveReplies(pageable, 1L, userVO);
    }

    @Test
    void getCountOfReplies() throws Exception {
        mockMvc.perform(get(ecoNewsCommentControllerLink + "/count/replies/{parentCommentId}", 1))
            .andExpect(status().isOk());

        verify(ecoNewsCommentService).countReplies(1L);
    }

    @Test
    void deleteTest() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(delete(ecoNewsCommentControllerLink + "?id=1")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(ecoNewsCommentService).deleteById(1L, userVO);
    }

    @Test
    void update() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        String textComment = "updated text";

        mockMvc.perform(patch(ecoNewsCommentControllerLink)
            .param("id", "1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(textComment)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(ecoNewsCommentService).update(textComment, 1L, userVO);

        mockMvc.perform(patch(ecoNewsCommentControllerLink))
            .andExpect(status().isBadRequest());
    }

    @Test
    void like() throws Exception {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(ecoNewsCommentControllerLink + "/like?id=1")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(ecoNewsCommentService).like(1L, userVO);
    }
}

package greencity.controller;

import greencity.config.SecurityConfig;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.entity.User;
import greencity.service.TipsAndTricksCommentService;
import greencity.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
public class TipsAndTricksCommentControllerTest {
    private static final String tipsAndTricksCommentLink = "/tipsandtricks/comments";
    private MockMvc mockMvc;
    @InjectMocks
    private TipsAndTricksCommentController tipsAndTricksCommentController;
    @Mock
    private TipsAndTricksCommentService tipsAndTricksCommentService;
    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(tipsAndTricksCommentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    public void saveTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("");
        when(userService.findByEmail(anyString())).thenReturn(new User());
        when(tipsAndTricksCommentService
                .save(anyLong(), any(AddTipsAndTricksCommentDtoRequest.class), any(User.class)))
                .thenReturn(new AddTipsAndTricksCommentDtoResponse());
        mockMvc.perform(post(tipsAndTricksCommentLink + "/{tipsAndTricksId}", 1)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"parentCommentId\": 0,\n" +
                        "  \"text\": \"string\"\n" +
                        "}"))
                .andExpect(status().isCreated());
        verify(tipsAndTricksCommentService, times(1))
                .save(anyLong(), any(AddTipsAndTricksCommentDtoRequest.class), any(User.class));
    }

    @Test
    public void saveBadRequestTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("");
        when(userService.findByEmail(anyString())).thenReturn(new User());
        when(tipsAndTricksCommentService
                .save(anyLong(), any(AddTipsAndTricksCommentDtoRequest.class), any(User.class)))
                .thenReturn(new AddTipsAndTricksCommentDtoResponse());
        mockMvc.perform(post(tipsAndTricksCommentLink + "/{tipsAndTricksId}", 1)
                .principal(principal)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("");
        when(userService.findByEmail(anyString())).thenReturn(new User());

        List<TipsAndTricksCommentDto> tipsAndTricksCommentDtos = Collections.singletonList(new TipsAndTricksCommentDto());
        PageableDto<TipsAndTricksCommentDto> pageableDto = new PageableDto<>(tipsAndTricksCommentDtos, tipsAndTricksCommentDtos.size(), 0);

        when(tipsAndTricksCommentService.findAllComments(any(Pageable.class), any(User.class), anyLong()))
                .thenReturn(pageableDto);
        mockMvc.perform(get(tipsAndTricksCommentLink + "?tipsAndTricksId=1")
                .principal(principal))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1)).findAllComments(any(Pageable.class), any(User.class), anyLong());
    }

    @Test
    public void getCountOfCommentsTest() throws Exception {
        when(tipsAndTricksCommentService.countComments(anyLong())).thenReturn(1);
        mockMvc.perform(get(tipsAndTricksCommentLink + "/count/comments?id=1"))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1)).countComments(anyLong());
    }

    @Test
    public void findAllRepliesTest() throws Exception {
        List<TipsAndTricksCommentDto> tipsAndTricksCommentDtos = Collections.singletonList(new TipsAndTricksCommentDto());
        when(tipsAndTricksCommentService.findAllReplies(anyLong())).thenReturn(tipsAndTricksCommentDtos);
        mockMvc.perform(get(tipsAndTricksCommentLink + "/replies/{parentCommentId}", 1))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1)).findAllReplies(anyLong());
    }

    @Test
    public void deleteTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("");
        when(userService.findByEmail(anyString())).thenReturn(new User());
        mockMvc.perform(delete(tipsAndTricksCommentLink + "?id=1")
                .principal(principal))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1)).deleteById(anyLong(), any(User.class));
    }

    @Test
    public void updateTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("");
        when(userService.findByEmail(anyString())).thenReturn(new User());
        mockMvc.perform(patch(tipsAndTricksCommentLink + "?id=1&text=text")
                .principal(principal))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1))
                .update(anyString(), anyLong(), any(User.class));
    }

    @Test
    public void likeTest() throws Exception {
        Principal principal = Mockito.mock(Principal.class);
        when(principal.getName()).thenReturn("");
        when(userService.findByEmail(anyString())).thenReturn(new User());
        mockMvc.perform(post(tipsAndTricksCommentLink + "/like?id=1")
                .principal(principal))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1))
                .like(anyLong(), any(User.class));
    }

    @Test
    public void getCountOfLikesTest() throws Exception {
        when(tipsAndTricksCommentService.countLikes(anyLong())).thenReturn(1);
        mockMvc.perform(get(tipsAndTricksCommentLink + "/count/likes?id=1"))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1))
                .countLikes(anyLong());
    }

    @Test
    public void getCountOfRepliesTest() throws Exception {
        when(tipsAndTricksCommentService.countReplies(anyLong())).thenReturn(1);
        mockMvc.perform(get(tipsAndTricksCommentLink + "/count/replies?parentCommentId=1"))
                .andExpect(status().isOk());
        verify(tipsAndTricksCommentService, times(1))
                .countReplies(anyLong());
    }
}

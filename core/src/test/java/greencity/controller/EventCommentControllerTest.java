package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import greencity.exception.exceptions.NotFoundException;
import greencity.service.EventCommentService;
import greencity.service.UserService;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
class EventCommentControllerTest {
    private static final String EVENT_COMMENT_CONTROLLER_LINK = "/events/comments";
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
    @SneakyThrows
    void save() {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
        String content = "{\n"
            + "  \"text\": \"string\",\n"
            + "  \"parentCommentId\": \"100\"\n"
            + "}";

        mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/{eventId}", 1)
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
    @SneakyThrows
    void saveBadRequestTest() {
        mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/{eventId}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getEventCommentById() {
        String content = "{\n"
            + "  \"text\": \"string\"\n"
            + "}";
        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getAllActiveComments() {
        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/active?eventId=1&page=5")
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(eventCommentService).getAllActiveComments(pageable, userVO, 1L);
    }

    @Test
    @SneakyThrows
    void countComments() {
        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/count/{eventId}", 1))
            .andExpect(status().isOk());

        verify(eventCommentService).countComments(1L);
    }

    @Test
    @SneakyThrows
    void findAllActiveReplies() {
        Long parentCommentId = 1L;
        int pageNumber = 0;
        int pageSize = 20;

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageableDto<EventCommentDto> eventCommentReplies = getPageableEventCommentDtos();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String expectedJson = objectMapper.writeValueAsString(eventCommentReplies);

        when(eventCommentService.findAllActiveReplies(pageable, parentCommentId, userVO))
            .thenReturn(eventCommentReplies);

        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/replies/active/{parentCommentId}", parentCommentId)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
        verify(eventCommentService).findAllActiveReplies(pageable, parentCommentId, userVO);
        verify(userService).findByEmail(principal.getName());
    }

    @Test
    @SneakyThrows
    void findAllActiveRepliesWithNotValidIdBadRequestTest() {
        String notValidId = "id";
        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/replies/active/{parentCommentId}", notValidId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void findAllActiveRepliesWithNonexistentIdNotFoundTest() {
        Long parentCommentId = 1L;

        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(eventCommentService)
            .findAllActiveReplies(pageable, parentCommentId, userVO);

        Assertions.assertThatThrownBy(
            () -> mockMvc
                .perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/replies/active/{parentCommentId}", parentCommentId)
                    .principal(principal))
                .andExpect(status().isNotFound()))
            .hasCause(new NotFoundException(errorMessage));
    }

    @Test
    @SneakyThrows
    void getCountOfActiveRepliesTest() {
        Long parentCommentId = 1L;
        int repliesAmount = 10;
        String expectedResponse = "<Integer>10</Integer>";
        when(eventCommentService.countAllActiveReplies(parentCommentId)).thenReturn(repliesAmount);

        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/replies/active/count/{parentCommentId}", parentCommentId))
            .andExpect(status().isOk())
            .andExpect(content().xml(expectedResponse));

        verify(eventCommentService).countAllActiveReplies(parentCommentId);
    }

    @Test
    @SneakyThrows
    void getCountOfActiveRepliesBadRequestTest() {
        String notValidId = "id";
        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/replies/active/count/{parentCommentId}", notValidId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getCountOfActiveRepliesNotFoundTest() {
        Long parentCommentId = 1L;

        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(eventCommentService)
            .countAllActiveReplies(parentCommentId);

        Assertions.assertThatThrownBy(
            () -> mockMvc
                .perform(
                    get(EVENT_COMMENT_CONTROLLER_LINK + "/replies/active/count/{parentCommentId}", parentCommentId))
                .andExpect(status().isNotFound()))
            .hasCause(new NotFoundException(errorMessage));
    }

    @Test
    @SneakyThrows
    void likeTest() {
        String commentId = "1";
        Long numericCommentId = Long.valueOf(commentId);

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/like")
            .param("commentId", commentId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(eventCommentService).like(numericCommentId, userVO);
    }

    @Test
    @SneakyThrows
    void likeWithNotValidIdBadRequestTest() {
        String notValidId = "id";

        mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/like")
            .param("commentId", notValidId)
            .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void likeNotFoundTest() {
        Long commentId = 1L;
        String commentIdParam = "1";

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(eventCommentService)
            .like(commentId, userVO);

        Assertions.assertThatThrownBy(
            () -> mockMvc.perform(post(EVENT_COMMENT_CONTROLLER_LINK + "/like")
                .param("commentId", commentIdParam)
                .principal(principal))
                .andExpect(status().isNotFound()))
            .hasCause(new NotFoundException(errorMessage));
    }

    @Test
    @SneakyThrows
    void countLikesTest() {
        Long commentId = 1L;
        int likesAmount = 10;

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        AmountCommentLikesDto result = AmountCommentLikesDto.builder()
            .id(commentId)
            .amountLikes(likesAmount)
            .isLiked(false)
            .userId(userVO.getId())
            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String expectedJson = objectMapper.writeValueAsString(result);

        when(eventCommentService.countLikes(commentId, userVO)).thenReturn(result);

        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/likes/count/{commentId}", commentId)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));

        verify(eventCommentService).countLikes(commentId, userVO);
    }

    @Test
    @SneakyThrows
    void countLikesNotValidIdBadRequestTest() {
        String notValidId = "id";
        mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/likes/count/{commentId}", notValidId)
            .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void countLikesNotFoundTest() {
        Long commentId = 1L;

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(eventCommentService)
            .countLikes(commentId, userVO);

        Assertions.assertThatThrownBy(
            () -> mockMvc.perform(get(EVENT_COMMENT_CONTROLLER_LINK + "/likes/count/{commentId}", commentId)
                .principal(principal))
                .andExpect(status().isNotFound()))
            .hasCause(new NotFoundException(errorMessage));
    }

    private PageableDto<EventCommentDto> getPageableEventCommentDtos() {
        List<EventCommentDto> eventCommentDtos = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            EventCommentDto eventComment = EventCommentDto.builder()
                .id((long) i)
                .text("Comment #" + i)
                .modifiedDate(LocalDateTime.now().minusDays(i))
                .author(EventCommentAuthorDto.builder()
                    .id(1L)
                    .name("UserName")
                    .userProfilePicturePath("PicturePath")
                    .build())
                .currentUserLiked(false)
                .build();
            eventCommentDtos.add(eventComment);
        }

        return new PageableDto<>(
            eventCommentDtos,
            eventCommentDtos.size(),
            1,
            1);
    }

}

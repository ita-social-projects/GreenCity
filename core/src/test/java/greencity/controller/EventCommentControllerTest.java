package greencity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import greencity.config.SecurityConfig;
import greencity.converters.UserArgumentResolver;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AmountCommentLikesDto;
import greencity.dto.comment.CommentDto;
import greencity.dto.user.UserVO;
import greencity.enums.ArticleType;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.NotFoundException;
import greencity.service.CommentService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import static greencity.ModelUtils.getPageableCommentDtos;
import static greencity.ModelUtils.getPrincipal;
import static greencity.ModelUtils.getUserVO;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ContextConfiguration
@Import(SecurityConfig.class)
class EventCommentControllerTest {
    private static final String EVENT_COMMENTS_CONTROLLER_LINK = "/events/comments";
    private static final String EVENT_ID_COMMENT_CONTROLLER_LINK = "/events/{eventId}/comments";
    private MockMvc mockMvc;
    @InjectMocks
    private EventCommentController eventCommentController;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private CommentService commentService;
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
        Long eventId = 1L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
        String content = "{\n"
            + "  \"text\": \"string\",\n"
            + "  \"parentCommentId\": \"100\"\n"
            + "}";

        MockMultipartFile jsonFile = new MockMultipartFile(
            "request",
            "",
            "application/json",
            content.getBytes());

        MockMultipartFile imageFile = new MockMultipartFile(
            "images",
            "image.jpg",
            "image/jpeg",
            "image data".getBytes());

        mockMvc.perform(multipart(EVENT_ID_COMMENT_CONTROLLER_LINK, eventId)
            .file(jsonFile)
            .file(imageFile)
            .principal(principal)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .content(content))
            .andExpect(status().isCreated());

        ObjectMapper mapper = new ObjectMapper();
        AddCommentDtoRequest addCommentDtoRequest =
            mapper.readValue(content, AddCommentDtoRequest.class);

        verify(userService).findByEmail("test@gmail.com");
        verify(commentService).save(ArticleType.EVENT, eventId, addCommentDtoRequest, new MultipartFile[] {imageFile},
            userVO, Locale.of("en"));
    }

    @Test
    @SneakyThrows
    void saveBadRequestTest() {
        mockMvc.perform(post(EVENT_ID_COMMENT_CONTROLLER_LINK, 1)
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void updateTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, UserVO.class)).thenReturn(userVO);
        String content = "string";

        mockMvc.perform(patch(EVENT_COMMENTS_CONTROLLER_LINK + "/1")
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(commentService).update("string", commentId, userVO);
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        Long commentId = 1L;
        Long eventId = 1L;
        UserVO userVO = getUserVO();
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(delete(EVENT_COMMENTS_CONTROLLER_LINK + "/{commentId}", eventId, commentId)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(commentService).delete(commentId, userVO);
    }

    @Test
    @SneakyThrows
    void getEventCommentById() {
        mockMvc.perform(get(EVENT_COMMENTS_CONTROLLER_LINK + "/{commentId}", 1))
            .andExpect(status().isOk());

        verify(commentService).getCommentById(ArticleType.EVENT, 1L, null);
    }

    @Test
    @SneakyThrows
    void getEventCommentByIdWithUser() {
        UserVO userVO = getUserVO();

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        mockMvc.perform(get(EVENT_COMMENTS_CONTROLLER_LINK + "/{commentId}", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(commentService).getCommentById(ArticleType.EVENT, 1L, userVO);
    }

    @Test
    @SneakyThrows
    void getAllActiveComments() {
        UserVO userVO = getUserVO();

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        int pageNumber = 5;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        mockMvc.perform(get(EVENT_ID_COMMENT_CONTROLLER_LINK + "?statuses=EDITED,ORIGINAL&page=5&size=20", 1)
            .principal(principal))
            .andExpect(status().isOk());

        verify(userService).findByEmail("test@gmail.com");
        verify(commentService).getAllActiveComments(pageable, userVO, 1L, ArticleType.EVENT);
    }

    @Test
    @SneakyThrows
    void countCommentsForEvent() {
        mockMvc.perform(get(EVENT_ID_COMMENT_CONTROLLER_LINK + "/count", 1))
            .andExpect(status().isOk());

        verify(commentService).countCommentsForEvent(1L);
    }

    @Test
    @SneakyThrows
    void findAllReplies() {
        Long parentCommentId = 1L;
        Long eventId = 1L;
        int pageNumber = 0;
        int pageSize = 20;
        List<CommentStatus> statuses = List.of(CommentStatus.EDITED, CommentStatus.ORIGINAL);
        UserVO userVO = getUserVO();

        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        PageableDto<CommentDto> commentReplies = getPageableCommentDtos();

        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        String expectedJson = objectMapper.writeValueAsString(commentReplies);

        when(commentService.getAllActiveReplies(pageable, parentCommentId, userVO))
            .thenReturn(commentReplies);

        mockMvc
            .perform(
                get(EVENT_ID_COMMENT_CONTROLLER_LINK + "/{parentCommentId}/replies/active?statuses=EDITED,ORIGINAL",
                    eventId,
                    parentCommentId)
                    .principal(principal)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));
        verify(commentService).getAllActiveReplies(pageable, parentCommentId, userVO);
        verify(userService).findByEmail(principal.getName());
    }

    @Test
    @SneakyThrows
    void findAllRepliesWithNotValidIdBadRequestTest() {
        String notValidId = "id";
        Long eventId = 1L;
        mockMvc
            .perform(get(EVENT_ID_COMMENT_CONTROLLER_LINK + "/{parentCommentId}/replies/active", eventId, notValidId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void findAllActiveRepliesWithNonexistentIdNotFoundTest() {
        Long parentCommentId = 1L;
        Long eventId = 1L;

        int pageNumber = 0;
        int pageSize = 20;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        UserVO userVO = getUserVO();
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(commentService)
            .getAllActiveReplies(pageable, parentCommentId, userVO);

        Assertions.assertThatThrownBy(
            () -> mockMvc
                .perform(get(
                    EVENT_ID_COMMENT_CONTROLLER_LINK + "/{parentCommentId}/replies/active/?statuses=ORIGINAL,EDITED",
                    eventId, parentCommentId)
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
        when(commentService.countAllActiveReplies(parentCommentId)).thenReturn(repliesAmount);

        mockMvc
            .perform(get(EVENT_COMMENTS_CONTROLLER_LINK + "/{parentCommentId}/replies/count", parentCommentId))
            .andExpect(status().isOk())
            .andExpect(content().xml(expectedResponse));

        verify(commentService).countAllActiveReplies(parentCommentId);
    }

    @Test
    @SneakyThrows
    void getCountOfActiveRepliesBadRequestTest() {
        String notValidId = "id";
        mockMvc.perform(get(EVENT_COMMENTS_CONTROLLER_LINK + "/{parentCommentId}/replies/count", notValidId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void getCountOfActiveRepliesNotFoundTest() {
        Long parentCommentId = 1L;
        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(commentService)
            .countAllActiveReplies(parentCommentId);

        Assertions.assertThatThrownBy(
            () -> mockMvc
                .perform(
                    get(EVENT_COMMENTS_CONTROLLER_LINK + "/{parentCommentId}/replies/count", parentCommentId))
                .andExpect(status().isNotFound()))
            .hasCause(new NotFoundException(errorMessage));
    }

    @Test
    @SneakyThrows
    void likeTest() {
        Long commentId = 1L;

        UserVO userVO = getUserVO();
        when(userService.findByEmail(anyString())).thenReturn(userVO);

        mockMvc.perform(post(EVENT_COMMENTS_CONTROLLER_LINK + "/like" + "/1")
            .principal(principal))
            .andExpect(status().isOk());

        verify(commentService).like(commentId, userVO);
    }

    @Test
    @SneakyThrows
    void likeWithNotValidIdBadRequestTest() {
        String notValidId = "id";

        mockMvc.perform(post(EVENT_COMMENTS_CONTROLLER_LINK + "/like/" + notValidId)
            .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void likeNotFoundTest() {
        Long commentId = 1L;

        UserVO userVO = getUserVO();
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(commentService)
            .like(commentId, userVO);

        Assertions.assertThatThrownBy(
            () -> mockMvc.perform(post(EVENT_COMMENTS_CONTROLLER_LINK + "/like/" + commentId)
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
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        AmountCommentLikesDto result = AmountCommentLikesDto.builder()
            .id(commentId)
            .amountLikes(likesAmount)
            .isLiked(false)
            .userId(userVO.getId())
            .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String expectedJson = objectMapper.writeValueAsString(result);

        when(commentService.countLikes(commentId, userVO)).thenReturn(result);

        mockMvc.perform(get(EVENT_COMMENTS_CONTROLLER_LINK + "/{commentId}/likes/count", commentId)
            .principal(principal)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedJson));

        verify(commentService).countLikes(commentId, userVO);
    }

    @Test
    @SneakyThrows
    void countLikesNotValidIdBadRequestTest() {
        String notValidId = "id";
        mockMvc.perform(get(EVENT_COMMENTS_CONTROLLER_LINK + "/{commentId}/likes/count", notValidId)
            .principal(principal))
            .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    void countLikesNotFoundTest() {
        Long commentId = 1L;

        UserVO userVO = getUserVO();
        when(userService.findByEmail(principal.getName())).thenReturn(userVO);

        String errorMessage = "ErrorMessage";

        doThrow(new NotFoundException(errorMessage))
            .when(commentService)
            .countLikes(commentId, userVO);

        Assertions.assertThatThrownBy(
            () -> mockMvc.perform(get(EVENT_COMMENTS_CONTROLLER_LINK + "/{commentId}/likes/count", commentId)
                .principal(principal))
                .andExpect(status().isNotFound()))
            .hasCause(new NotFoundException(errorMessage));
    }

}

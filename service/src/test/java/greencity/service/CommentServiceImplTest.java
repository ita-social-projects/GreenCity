package greencity.service;

import greencity.ModelUtils;
import greencity.achievement.AchievementCalculation;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.*;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.user.UserVO;
import greencity.entity.*;
import greencity.entity.event.Event;
import greencity.enums.ArticleType;
import greencity.enums.CommentStatus;
import greencity.enums.NotificationType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.message.UserTaggedInCommentMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static greencity.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    private CommentRepo commentRepo;
    @Mock
    private EventRepo eventRepo;
    @Mock
    private EcoNewsRepo econewsRepo;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    HabitRepo habitRepo;
    @Mock
    private HabitTranslationRepo habitTranslationRepo;
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private AchievementCalculation achievementCalculation;
    @Mock
    private UserNotificationService userNotificationService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private EventCommentServiceImpl eventCommentServiceImpl;

    @Test
    void save() {
        UserVO userVO = getUserVO();
        User user = getUser();
        Habit habit = ModelUtils.getHabit().setUserId(getUser().getId());
        AddCommentDtoRequest addCommentDtoRequest = ModelUtils.getAddCommentDtoRequest();
        Comment comment = getComment();
        CommentVO commentVO = getCommentVO();
        CommentAuthorDto commentAuthorDto = ModelUtils.getCommentAuthorDto();
        HabitTranslation habitTranslation = getHabitTranslation();

        when(habitRepo.findById(anyLong())).thenReturn(Optional.ofNullable(habit));
        when(commentRepo.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(commentRepo.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(userVO, CommentAuthorDto.class)).thenReturn(commentAuthorDto);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(addCommentDtoRequest, Comment.class)).thenReturn(comment);
        when(modelMapper.map(any(Comment.class), eq(AddCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddCommentDtoResponse());
        when(modelMapper.map(any(Comment.class), eq(CommentVO.class))).thenReturn(commentVO);
        when(habitTranslationRepo.findByHabitAndLanguageCode(habit, Locale.of("en").getLanguage()))
            .thenReturn(Optional.of(habitTranslation));

        doNothing().when(userNotificationService).createNotification(
            any(UserVO.class), any(UserVO.class), any(NotificationType.class),
            anyLong(), anyString(), anyLong(), anyString());

        commentService.save(ArticleType.HABIT, 1L, addCommentDtoRequest, userVO, Locale.of("en"));
        assertEquals(CommentStatus.ORIGINAL, comment.getStatus());

        verify(habitRepo, times(5)).findById(anyLong());
        verify(commentRepo).save(any(Comment.class));
        verify(commentRepo).findById(anyLong());
        verify(userRepo, times(3)).findById(anyLong());
        verify(modelMapper).map(userVO, CommentAuthorDto.class);
        verify(modelMapper).map(userVO, User.class);
        verify(modelMapper).map(addCommentDtoRequest, Comment.class);
        verify(modelMapper).map(any(Comment.class), eq(AddCommentDtoResponse.class));
    }

    @Test
    void sendNotificationIfUserTaggedInComment() {
        String commentText = "test data-userid=\"5\" test";
        UserVO userVO = getUserVO();
        User user = getUser();
        Habit habit = ModelUtils.getHabit().setUserId(getUser().getId());
        HabitTranslation habitTranslation = getHabitTranslation();
        Comment comment = getComment();
        CommentVO commentVO = getCommentVO().setText(commentText);
        AddCommentDtoResponse response = getAddCommentDtoResponse().setText(commentText);
        AddCommentDtoRequest addCommentDtoRequest = AddCommentDtoRequest.builder()
            .text(commentText)
            .build();
        ArticleType articleType = ArticleType.HABIT;
        CommentAuthorDto commentAuthorDto = ModelUtils.getCommentAuthorDto();
        Set<Long> userIds = Set.of(5L);

        when(modelMapper.map(any(User.class), eq(UserVO.class))).thenReturn(userVO);
        when(modelMapper.map(any(UserVO.class), eq(User.class))).thenReturn(user);
        when(modelMapper.map(any(UserVO.class), eq(CommentAuthorDto.class))).thenReturn(commentAuthorDto);
        when(modelMapper.map(any(Comment.class), eq(CommentVO.class))).thenReturn(commentVO);
        when(modelMapper.map(any(CommentVO.class), eq(Comment.class))).thenReturn(comment);
        when(commentRepo.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(User.builder()
            .id(5L)
            .email("test@email.com")
            .build()));
        when(modelMapper.map(addCommentDtoRequest, Comment.class)).thenReturn(comment.setText(commentText));
        when(modelMapper.map(comment, AddCommentDtoResponse.class)).thenReturn(response);
        when(habitRepo.findById(anyLong())).thenReturn(Optional.ofNullable(habit));
        when(habitTranslationRepo.findByHabitAndLanguageCode(habit, Locale.of("en").getLanguage()))
            .thenReturn(Optional.of(habitTranslation));
        when(eventCommentServiceImpl.getUserIdFromComment(commentText)).thenReturn(userIds);

        commentService.save(articleType, 1L, addCommentDtoRequest, userVO, Locale.of("en"));

        verify(notificationService, times(1))
            .sendUsersTaggedInCommentEmailNotification(any(UserTaggedInCommentMessage.class));

        verify(commentRepo, times(1)).save(any(Comment.class));
    }

    @Test
    void saveReplyWithWrongParentIdThrowException() {
        Long parentCommentId = 123L;
        UserVO userVO = getUserVO();
        User user = getUser();
        Habit habit = ModelUtils.getHabit().setUserId(getUser().getId());
        AddCommentDtoRequest addCommentDtoRequest = ModelUtils.getAddCommentDtoRequest();
        addCommentDtoRequest.setParentCommentId(parentCommentId);
        Comment comment = getComment();

        when(habitRepo.findById(anyLong())).thenReturn(Optional.ofNullable(habit));
        when(commentRepo.findById(parentCommentId)).thenReturn(Optional.empty());
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(addCommentDtoRequest, Comment.class)).thenReturn(comment);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
            () -> commentService.save(ArticleType.HABIT, 1L, addCommentDtoRequest, userVO, Locale.ENGLISH));

        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + parentCommentId, notFoundException.getMessage());

        verify(habitRepo).findById(anyLong());
        verify(commentRepo).findById(parentCommentId);
        verify(userRepo).findById(anyLong());
        verify(modelMapper).map(userVO, User.class);
        verify(modelMapper).map(addCommentDtoRequest, Comment.class);
    }

    @Test
    void saveReplyWithWrongEventIdThrowException() {
        Long parentCommentId = 123L;
        Long replyHabitId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        AddCommentDtoRequest addCommentDtoRequest = ModelUtils.getAddCommentDtoRequest();
        addCommentDtoRequest.setParentCommentId(parentCommentId);
        Comment comment = getComment();
        Habit habit = ModelUtils.getHabit().setUserId(getUser().getId());
        habit.setId(replyHabitId);
        Habit parentCommentHabit = ModelUtils.getHabit().setUserId(getUser().getId());
        parentCommentHabit.setId(2L);
        Comment parentComment = getComment();
        parentComment.setId(parentCommentId);
        parentComment.setArticleType(ArticleType.HABIT);
        parentComment.setArticleId(parentCommentHabit.getId());

        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(habit));
        when(commentRepo.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(addCommentDtoRequest, Comment.class)).thenReturn(comment);

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class,
                () -> commentService.save(ArticleType.HABIT, replyHabitId, addCommentDtoRequest, userVO,
                    Locale.ENGLISH));

        String expectedErrorMessage = ErrorMessage.COMMENT_NOT_FOUND_BY_ID + parentCommentId
            + " in Habit with id: " + habit.getId();
        assertEquals(expectedErrorMessage, notFoundException.getMessage());

        verify(habitRepo).findById(anyLong());
        verify(commentRepo).findById(parentCommentId);
        verify(userRepo).findById(anyLong());
        verify(modelMapper).map(userVO, User.class);
        verify(modelMapper).map(addCommentDtoRequest, Comment.class);
    }

    @Test
    void saveReplyForReplyThrowException() {
        Long parentCommentId = 123L;
        Long replyHabitId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        AddCommentDtoRequest addCommentDtoRequest = ModelUtils.getAddCommentDtoRequest();
        addCommentDtoRequest.setParentCommentId(parentCommentId);

        Comment comment = getComment();

        Habit habit = ModelUtils.getHabit().setUserId(getUser().getId());
        habit.setId(replyHabitId);

        Comment parentComment = getComment();
        parentComment.setId(parentCommentId);
        parentComment.setArticleType(ArticleType.HABIT);
        parentComment.setArticleId(habit.getId());

        parentComment.setParentComment(getComment());

        when(habitRepo.findById(anyLong())).thenReturn(Optional.of(habit));
        when(commentRepo.findById(parentCommentId)).thenReturn(Optional.of(parentComment));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(addCommentDtoRequest, Comment.class)).thenReturn(comment);

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> commentService.save(ArticleType.HABIT, replyHabitId, addCommentDtoRequest, userVO,
                    Locale.ENGLISH));

        String expectedErrorMessage = ErrorMessage.CANNOT_REPLY_THE_REPLY;

        assertEquals(expectedErrorMessage, badRequestException.getMessage());

        verify(habitRepo).findById(anyLong());
        verify(commentRepo).findById(parentCommentId);
        verify(userRepo).findById(anyLong());
        verify(modelMapper).map(userVO, User.class);
        verify(modelMapper).map(addCommentDtoRequest, Comment.class);
    }

    @Test
    void getCommentById() {
        Comment comment = getComment();
        CommentDto commentDto = getCommentDto();

        when(commentRepo.findById(1L)).thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        assertEquals(commentDto, commentService.getCommentById(comment.getArticleType(), 1L, getUserVO()));

        verify(commentRepo).findById(1L);
        verify(modelMapper).map(comment, CommentDto.class);
    }

    @Test
    void countCommentsForHabit() {
        Habit habit = getHabit();

        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(commentRepo.countNotDeletedCommentsByHabit(habit.getId())).thenReturn(1);

        assertEquals(1, commentService.countCommentsForHabit(habit.getId()));

        verify(habitRepo).findById(1L);
        verify(commentRepo).countNotDeletedCommentsByHabit(habit.getId());
    }

    @Test
    void countCommentsHabitNotFoundException() {
        Long habitId = 1L;

        when(habitRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.countCommentsForHabit(habitId));

        verify(habitRepo).findById(1L);
    }

    @Test
    void getAllActiveComments() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long habitId = 1L;
        Comment comment = getComment();
        Habit habit = ModelUtils.getHabit();
        Page<Comment> pages = new PageImpl<>(Collections.singletonList(comment), pageable, 1);
        CommentDto commentDto = ModelUtils.getCommentDto();

        when(habitRepo.findById(1L)).thenReturn(Optional.of(habit));
        when(commentRepo.findAllByParentCommentIdIsNullAndArticleIdAndArticleTypeAndStatusNotOrderByCreatedDateDesc(
            pageable, habitId, ArticleType.HABIT, CommentStatus.DELETED))
            .thenReturn(pages);
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        PageableDto<CommentDto> allComments = commentService.getAllActiveComments(
            pageable, userVO, habitId, ArticleType.HABIT);
        assertEquals(commentDto, allComments.getPage().getFirst());
        assertEquals(4, allComments.getTotalElements());
        assertEquals(1, allComments.getCurrentPage());
        assertEquals(1, allComments.getPage().size());

        verify(habitRepo).findById(1L);
        verify(commentRepo).findAllByParentCommentIdIsNullAndArticleIdAndArticleTypeAndStatusNotOrderByCreatedDateDesc(
            pageable, habitId, ArticleType.HABIT, CommentStatus.DELETED);
        verify(modelMapper).map(comment, CommentDto.class);

    }

    @Test
    void update() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        String editedText = "edited text";
        Comment comment = getComment();

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.ofNullable(comment));

        commentService.update(editedText, commentId, userVO);

        assertEquals(CommentStatus.EDITED, comment.getStatus());

        verify(commentRepo).save(any(Comment.class));
    }

    @Test
    void updateCommentThatDoesntExistsThrowException() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        String editedText = "edited text";

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class,
                () -> commentService.update(editedText, commentId, userVO));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void updateCommentThatDoesntBelongsToUserThrowException() {
        User user = ModelUtils.getUser();
        UserVO userVO = getUserVO();
        user.setId(2L);

        Long commentId = 1L;
        Comment comment = getComment();
        comment.setUser(user);
        String editedText = "edited text";

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.of(comment));

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> commentService.update(editedText, commentId, userVO));
        assertEquals(ErrorMessage.NOT_A_CURRENT_USER, badRequestException.getMessage());

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void delete() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        Comment comment = getComment();
        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.ofNullable(comment));
        commentService.delete(commentId, userVO);

        assertEquals(CommentStatus.DELETED, comment.getComments().getFirst().getStatus());
        assertEquals(CommentStatus.DELETED, comment.getStatus());

        verify(commentRepo).findByIdAndStatusNot(any(Long.class), eq(CommentStatus.DELETED));
    }

    @Test
    void deleteCommentUserHasNoPermissionThrowException() {
        Long commentId = 1L;

        User user = getUser();
        user.setId(2L);
        UserVO userToDeleteVO = getUserVO();

        Comment comment = getComment();
        comment.setUser(user);

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED))
            .thenReturn(Optional.of(comment));

        UserHasNoPermissionToAccessException noPermissionToAccessException =
            assertThrows(UserHasNoPermissionToAccessException.class,
                () -> commentService.delete(commentId, userToDeleteVO));
        assertEquals(ErrorMessage.USER_HAS_NO_PERMISSION, noPermissionToAccessException.getMessage());

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void deleteCommentThatDoesntExistsThrowException() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> commentService.delete(commentId, userVO));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId, notFoundException.getMessage());

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void findAllActiveRepliesTest() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long parentCommentId = 1L;

        Comment childComment = getComment();
        childComment.setParentComment(getComment());

        Page<Comment> page = new PageImpl<>(Collections.singletonList(childComment), pageable, 1);

        when(modelMapper.map(childComment, CommentDto.class)).thenReturn(ModelUtils.getCommentDto());
        when(commentRepo.findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
            CommentStatus.DELETED))
            .thenReturn(page);

        PageableDto<CommentDto> commentDtos =
            commentService.getAllActiveReplies(pageable, parentCommentId, userVO);
        assertEquals(getComment().getId(), commentDtos.getPage().getFirst().getId());
        assertEquals(4, commentDtos.getTotalElements());
        assertEquals(1, commentDtos.getCurrentPage());
        assertEquals(1, commentDtos.getPage().size());

        verify(modelMapper).map(childComment, CommentDto.class);
        verify(commentRepo).findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(
            pageable, parentCommentId, CommentStatus.DELETED);
    }

    @Test
    void findAllActiveRepliesCurrentUserLikedTest() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        User user = getUser();
        Long parentCommentId = 1L;

        Comment childComment = getComment();
        childComment.setParentComment(getComment());
        childComment.setUsersLiked(new HashSet<>(Collections.singletonList(user)));

        Page<Comment> page = new PageImpl<>(Collections.singletonList(childComment), pageable, 1);

        when(commentRepo.findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageable, parentCommentId,
            CommentStatus.DELETED))
            .thenReturn(page);
        when(modelMapper.map(childComment, CommentDto.class)).thenReturn(getCommentDto());

        commentService.getAllActiveReplies(pageable, parentCommentId, userVO);

        assertTrue(childComment.isCurrentUserLiked());

        verify(commentRepo).findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(
            pageable, parentCommentId, CommentStatus.DELETED);
        verify(modelMapper).map(childComment, CommentDto.class);
    }

    @Test
    void countAllActiveRepliesTest() {
        Long parentCommentId = 1L;
        int repliesAmount = 5;
        when(commentRepo.findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED))
            .thenReturn(Optional.of(getComment()));
        when(commentRepo.countByParentCommentIdAndStatusNot(parentCommentId, CommentStatus.DELETED))
            .thenReturn(repliesAmount);

        int result = commentService.countAllActiveReplies(parentCommentId);
        assertEquals(repliesAmount, result);

        verify(commentRepo).findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED);
        verify(commentRepo).countByParentCommentIdAndStatusNot(parentCommentId, CommentStatus.DELETED);
    }

    @Test
    void countAllActiveRepliesNotFoundParentCommentTest() {
        Long parentCommentId = 1L;
        when(commentRepo.findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED))
            .thenReturn(Optional.empty());
        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> commentService.countAllActiveReplies(parentCommentId));

        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + parentCommentId, notFoundException.getMessage());

        verify(commentRepo).findByIdAndStatusNot(parentCommentId, CommentStatus.DELETED);
    }

    @Test
    void likeTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        Comment comment = getComment();

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.of(comment));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        commentService.like(commentId, userVO);

        assertTrue(comment.getUsersLiked().contains(user));

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
        verify(modelMapper).map(userVO, User.class);
    }

    @Test
    void unlikeTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();
        User user = getUser();
        Comment comment = getComment();
        comment.setCurrentUserLiked(true);
        comment.getUsersLiked().add(user);
        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.of(comment));

        commentService.like(commentId, userVO);

        assertFalse(comment.getUsersLiked().contains(user));

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void likeNotFoundCommentTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> commentService.like(commentId, userVO));

        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId, notFoundException.getMessage());

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void countLikesTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();
        Comment comment = getComment();
        Integer usersLikedAmount = 5;

        for (long i = 0; i < usersLikedAmount; i++) {
            User user = getUser();
            user.setId(i);
            comment.getUsersLiked().add(user);
        }

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.of(comment));

        AmountCommentLikesDto result = commentService.countLikes(commentId, userVO);

        assertEquals(commentId, result.getId());
        assertEquals(comment.getUser().getId(), result.getUserId());
        assertEquals(usersLikedAmount, result.getAmountLikes());
        assertTrue(result.isLiked());

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void countLikesNotFoundCommentTest() {
        Long commentId = 1L;
        UserVO userVO = getUserVO();

        when(commentRepo.findByIdAndStatusNot(commentId, CommentStatus.DELETED)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> commentService.countLikes(commentId, userVO));

        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_BY_ID + commentId, notFoundException.getMessage());

        verify(commentRepo).findByIdAndStatusNot(commentId, CommentStatus.DELETED);
    }

    @Test
    void getEventAuthorTest() {
        Long articleId = 1L;
        Event event = getEvent();
        User user = getUser();

        when(eventRepo.findById(articleId)).thenReturn(Optional.of(event));
        when(userRepo.findById(getUser().getId())).thenReturn(Optional.of(user));

        commentService.getArticleAuthor(ArticleType.EVENT, articleId);

        verify(eventRepo).findById(articleId);
        verify(userRepo).findById(getUser().getId());
    }

    @Test
    void getEcoNewsAuthorTest() {
        Long articleId = 1L;
        EcoNews ecoNews = getEcoNews();
        User user = getUser();

        when(econewsRepo.findById(articleId)).thenReturn(Optional.of(ecoNews));
        when(userRepo.findById(getUser().getId())).thenReturn(Optional.of(user));

        commentService.getArticleAuthor(ArticleType.ECO_NEWS, articleId);

        verify(econewsRepo).findById(articleId);
        verify(userRepo).findById(getUser().getId());
    }

    @Test
    void getHabitAuthorTest() {
        Long articleId = 1L;
        Habit habit = getHabit();
        habit.setUserId(1L);
        User user = getUser();

        when(habitRepo.findById(articleId)).thenReturn(Optional.of(habit));
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));

        User result = commentService.getArticleAuthor(ArticleType.HABIT, articleId);

        assertEquals(user, result);
        verify(habitRepo).findById(articleId);
        verify(userRepo).findById(user.getId());
    }

    @Test
    void getArticleHabitTitleTest() {
        Long articleId = 1L;
        String expectedName = "Habit Title";
        Habit habit = new Habit();
        HabitTranslation habitTranslation = getHabitTranslation();
        habitTranslation.setName(expectedName);
        habit.setHabitTranslations(Collections.singletonList(habitTranslation));

        when(habitRepo.findById(articleId)).thenReturn(Optional.of(habit));
        when(habitTranslationRepo.findByHabitAndLanguageCode(habit, Locale.of("en").getLanguage()))
            .thenReturn(Optional.of(habitTranslation));

        String result = commentService.getArticleTitle(ArticleType.HABIT, articleId, Locale.of("en"));

        assertEquals(expectedName, result);
        verify(habitRepo).findById(articleId);
    }

    @Test
    void getArticleHabitTitleNotFoundTest() {
        Long articleId = 1L;

        when(habitRepo.findById(articleId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
            () -> commentService.getArticleTitle(ArticleType.HABIT, articleId, Locale.ENGLISH));

        verify(habitRepo).findById(articleId);
    }
}
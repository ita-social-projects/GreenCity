package greencity.service;

import greencity.ModelUtils;
import greencity.achievement.AchievementCalculation;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.AmountCommentLikesDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.EcoNews;
import greencity.entity.Habit;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.enums.ArticleType;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.rating.RatingCalculation;
import greencity.repository.CommentRepo;
import greencity.repository.EventRepo;
import greencity.repository.HabitRepo;
import greencity.repository.UserRepo;
import greencity.repository.EcoNewsRepo;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static greencity.ModelUtils.getUserVO;
import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getComment;
import static greencity.ModelUtils.getCommentDto;
import static greencity.ModelUtils.getHabit;
import static greencity.ModelUtils.getEvent;
import static greencity.ModelUtils.getEcoNews;
import static greencity.constant.ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID;
import static greencity.constant.ErrorMessage.HABIT_NOT_FOUND_BY_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

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
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private AchievementCalculation achievementCalculation;

    @Test
    void save() {
        UserVO userVO = getUserVO();
        User user = getUser();
        Habit habit = ModelUtils.getHabit().setUserId(getUser().getId());
        AddCommentDtoRequest addCommentDtoRequest = ModelUtils.getAddCommentDtoRequest();
        Comment comment = getComment();
        CommentAuthorDto commentAuthorDto = ModelUtils.getCommentAuthorDto();

        when(habitRepo.findById(anyLong())).thenReturn(Optional.ofNullable(habit));
        when(commentRepo.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(commentRepo.findById(anyLong())).thenReturn(Optional.of(comment));
        when(userRepo.findById(anyLong())).thenReturn(Optional.of(user));
        when(modelMapper.map(userVO, CommentAuthorDto.class)).thenReturn(commentAuthorDto);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(addCommentDtoRequest, Comment.class)).thenReturn(comment);
        when(modelMapper.map(any(Comment.class), eq(AddCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddCommentDtoResponse());

        commentService.save(ArticleType.HABIT, 1L, addCommentDtoRequest, userVO);
        assertEquals(CommentStatus.ORIGINAL, comment.getStatus());

        verify(habitRepo).findById(anyLong());
        verify(commentRepo).save(any(Comment.class));
        verify(commentRepo).findById(anyLong());
        verify(userRepo).findById(anyLong());
        verify(modelMapper).map(userVO, CommentAuthorDto.class);
        verify(modelMapper).map(userVO, User.class);
        verify(modelMapper).map(addCommentDtoRequest, Comment.class);
        verify(modelMapper).map(any(Comment.class), eq(AddCommentDtoResponse.class));
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

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class,
                () -> commentService.save(ArticleType.HABIT, 1L, addCommentDtoRequest, userVO));

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
                () -> commentService.save(ArticleType.HABIT, replyHabitId, addCommentDtoRequest, userVO));

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
                () -> commentService.save(ArticleType.HABIT, replyHabitId, addCommentDtoRequest, userVO));

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
    void countCommentsForEcoNews() {
        EcoNews ecoNews = getEcoNews();

        when(econewsRepo.findById(1L)).thenReturn(Optional.of(ecoNews));
        when(commentRepo.countNotDeletedCommentsByEcoNews(ecoNews.getId())).thenReturn(1);

        assertEquals(1, commentService.countCommentsForEcoNews(ecoNews.getId()));

        verify(econewsRepo).findById(1L);
        verify(commentRepo).countNotDeletedCommentsByEcoNews(ecoNews.getId());
    }

    @Test
    void countCommentsEcoNewsNotFoundException() {
        Long ecoNewsId = 1L;

        when(econewsRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.countCommentsForEcoNews(ecoNewsId));

        verify(econewsRepo).findById(1L);
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
    void getAllActiveCommentsEcoNews() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long ecoNewsId = 1L;
        EcoNews ecoNews = getEcoNews();
        Comment comment = getComment();
        CommentDto commentDto = ModelUtils.getCommentDto();
        Page<Comment> pages = new PageImpl<>(Collections.singletonList(comment), pageable, 1);

        when(econewsRepo.findById(ecoNewsId)).thenReturn(Optional.of(ecoNews));
        when(commentRepo.findAllByParentCommentIdIsNullAndArticleIdAndArticleTypeAndStatusNotOrderByCreatedDateDesc(
            pageable, ecoNewsId, ArticleType.ECO_NEWS, CommentStatus.DELETED))
            .thenReturn(pages);
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        PageableDto<CommentDto> allComments = commentService.getAllActiveComments(
            pageable, userVO, ecoNewsId, ArticleType.ECO_NEWS);

        assertEquals(commentDto, allComments.getPage().getFirst());
        assertEquals(4, allComments.getTotalElements());
        assertEquals(1, allComments.getCurrentPage());
        assertEquals(1, allComments.getPage().size());

        verify(econewsRepo).findById(ecoNewsId);
        verify(commentRepo).findAllByParentCommentIdIsNullAndArticleIdAndArticleTypeAndStatusNotOrderByCreatedDateDesc(
            pageable, ecoNewsId, ArticleType.ECO_NEWS, CommentStatus.DELETED);
        verify(modelMapper).map(comment, CommentDto.class);
    }

    @Test
    void getAllActiveComments_HabitNotFound() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long habitId = 1L;

        when(habitRepo.findById(habitId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> commentService.getAllActiveComments(pageable, userVO, habitId, ArticleType.HABIT));

        assertEquals(HABIT_NOT_FOUND_BY_ID + habitId, exception.getMessage());

        verify(habitRepo).findById(habitId);
    }

    @Test
    void getAllActiveComments_EcoNewsNotFound() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long ecoNewsId = 1L;

        when(econewsRepo.findById(ecoNewsId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> commentService.getAllActiveComments(pageable, userVO, ecoNewsId, ArticleType.ECO_NEWS));

        assertEquals(ECO_NEWS_NOT_FOUND_BY_ID + ecoNewsId, exception.getMessage());

        verify(econewsRepo).findById(ecoNewsId);
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
}
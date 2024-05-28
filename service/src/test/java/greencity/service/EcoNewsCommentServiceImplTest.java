package greencity.service;

import static greencity.ModelUtils.getTagUser;
import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserSearchDto;
import static greencity.ModelUtils.getUserTagDto;
import static greencity.ModelUtils.getUserVO;

import greencity.achievement.AchievementCalculation;
import greencity.dto.user.UserTagDto;
import greencity.enums.CommentStatus;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import jakarta.servlet.http.HttpServletRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.econews.EcoNewsVO;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.AmountCommentLikesDto;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.dto.econewscomment.EcoNewsCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.rating.RatingCalculation;
import greencity.repository.EcoNewsCommentRepo;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import greencity.repository.EcoNewsRepo;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;

@ExtendWith(MockitoExtension.class)
class EcoNewsCommentServiceImplTest {
    @Mock
    private EcoNewsCommentRepo ecoNewsCommentRepo;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    EcoNewsRepo ecoNewsRepo;
    @InjectMocks
    private EcoNewsCommentServiceImpl ecoNewsCommentService;
    @Mock
    private UserService userService;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private AchievementCalculation achievementCalculation;

    @Mock
    private UserRepo userRepo;

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserNotificationService userNotificationService;

    private String token = "token";

    @Test
    void saveCommentWithNoParentCommentId() {
        UserVO userVO = getUserVO();
        User user = getUser();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNewsVO);
        when(ecoNewsCommentRepo.save(any(EcoNewsComment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(modelMapper.map(any(EcoNewsComment.class), eq(AddEcoNewsCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddEcoNewsCommentDtoResponse());

        ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, userVO);
        verify(ecoNewsCommentRepo).save(any(EcoNewsComment.class));
    }

    @Test
    void saveCommentWithParentCommentId() {
        User user = getUser();
        UserVO userVO = getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        addEcoNewsCommentDtoRequest.setParentCommentId(2L);
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        EcoNewsComment ecoNewsCommentParent = EcoNewsComment.builder()
            .id(2L)
            .text("text")
            .createdDate(LocalDateTime.now())
            .modifiedDate(LocalDateTime.now())
            .user(user)
            .ecoNews(ecoNews)
            .build();

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNewsVO);
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(ecoNewsCommentRepo.save(any(EcoNewsComment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(ecoNewsVO, EcoNews.class)).thenReturn(ecoNews);
        when(modelMapper.map(any(EcoNewsComment.class), eq(AddEcoNewsCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddEcoNewsCommentDtoResponse());
        when(ecoNewsCommentRepo.findById(anyLong())).thenReturn(Optional.ofNullable(ecoNewsCommentParent));
        when(modelMapper.map(ecoNewsComment.getUser(), UserVO.class)).thenReturn(userVO);

        ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, userVO);
        verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void saveCommentThatHaveReplyWithAnotherReplyThrowException() {
        User user = getUser();
        UserVO userVO = getUserVO();
        EcoNews ecoNews = ModelUtils.getEcoNews();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        addEcoNewsCommentDtoRequest.setParentCommentId(2L);
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        EcoNewsComment ecoNewsCommentParent = EcoNewsComment.builder()
            .id(2L)
            .text("text")
            .createdDate(LocalDateTime.now())
            .modifiedDate(LocalDateTime.now())
            .parentComment(ecoNewsComment)
            .user(user)
            .ecoNews(ecoNews)
            .build();

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNewsVO);
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(ecoNewsCommentRepo.findById(anyLong())).thenReturn(Optional.ofNullable(ecoNewsCommentParent));

        BadRequestException badRequestException = assertThrows(BadRequestException.class,
            () -> ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, userVO));
        assertEquals(ErrorMessage.CANNOT_REPLY_THE_REPLY, badRequestException.getMessage());
    }

    @Test
    void saveCommentWithWrongParentIdThrowException() {
        UserVO userVO = getUserVO();
        EcoNewsVO ecoNewsVO = ModelUtils.getEcoNewsVO();
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        addEcoNewsCommentDtoRequest.setParentCommentId(2L);
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNewsVO);
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(ecoNewsCommentRepo.findById(anyLong())).thenReturn(Optional.empty());

        BadRequestException badRequestException = assertThrows(BadRequestException.class,
            () -> ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, userVO));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, badRequestException.getMessage());
    }

    @Test
    void findAllComments() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        User user = getUser();
        Long ecoNewsId = 1L;
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUsersLiked(new HashSet<>());
        Page<EcoNewsComment> pages = new PageImpl<>(Collections.singletonList(ecoNewsComment), pageable, 1);
        EcoNewsCommentDto ecoNewsCommentDto = ModelUtils.getEcoNewsCommentDto();

        when(ecoNewsService.findById(1L)).thenReturn(ModelUtils.getEcoNewsVO());
        when(ecoNewsCommentRepo.findAllByParentCommentIsNullAndEcoNewsIdOrderByCreatedDateDesc(pageable, ecoNewsId))
            .thenReturn(pages);
        when(modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class)).thenReturn(ecoNewsCommentDto);
        when(ecoNewsCommentRepo.countByParentCommentId(any())).thenReturn(0);

        PageableDto<EcoNewsCommentDto> allComments =
            ecoNewsCommentService.findAllComments(pageable, userVO, ecoNewsId);
        assertEquals(ecoNewsCommentDto, allComments.getPage().get(0));
        assertEquals(4, allComments.getTotalElements());
        assertEquals(1, allComments.getCurrentPage());
        assertEquals(1, allComments.getPage().size());
    }

    @Test
    void findAllReplies() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long parentCommentId = 1L;
        EcoNewsComment ecoNewsCommentChild = ModelUtils.getEcoNewsComment();
        ecoNewsCommentChild.setParentComment(ModelUtils.getEcoNewsComment());
        ecoNewsCommentChild.setUsersLiked(new HashSet<>());
        Page<EcoNewsComment> pages = new PageImpl<>(Collections.singletonList(ecoNewsCommentChild), pageable, 1);

        when(ecoNewsCommentRepo.findAllByParentCommentIdOrderByCreatedDateDesc(pageable, parentCommentId))
            .thenReturn(pages);
        when(modelMapper.map(ecoNewsCommentChild, EcoNewsCommentDto.class))
            .thenReturn(ModelUtils.getEcoNewsCommentDto());

        PageableDto<EcoNewsCommentDto> allReplies =
            ecoNewsCommentService.findAllReplies(pageable, parentCommentId, userVO);
        Assertions.assertEquals(ModelUtils.getEcoNewsCommentDto().getId(), allReplies.getPage().get(0).getId());
        assertEquals(4, allReplies.getTotalElements());
        assertEquals(1, allReplies.getCurrentPage());
        assertEquals(1, allReplies.getPage().size());

        verify(ecoNewsCommentRepo).findAllByParentCommentIdOrderByCreatedDateDesc(pageable, parentCommentId);
        verify(modelMapper).map(ecoNewsCommentChild, EcoNewsCommentDto.class);
    }

    @Test
    void findAllRepliesThrewException() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long parentCommentId = 1L;
        EcoNewsComment ecoNewsCommentChild = ModelUtils.getEcoNewsComment();
        ecoNewsCommentChild.setParentComment(ModelUtils.getEcoNewsComment());
        ecoNewsCommentChild.setUsersLiked(new HashSet<>());

        Page<EcoNewsComment> pages = new PageImpl<>(List.of(), pageable, 0);

        when(ecoNewsCommentRepo.findAllByParentCommentIdOrderByCreatedDateDesc(pageable, parentCommentId))
            .thenReturn(pages);

        assertThrows(NotFoundException.class,
            () -> ecoNewsCommentService.findAllReplies(pageable, parentCommentId, userVO));

        verify(ecoNewsCommentRepo).findAllByParentCommentIdOrderByCreatedDateDesc(pageable, parentCommentId);
    }

    @Test
    void userDeletesOwnComment() {
        UserVO userVO = getUserVO();
        User user = ModelUtils.getUser();
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId))
            .thenReturn(Optional.ofNullable(ModelUtils.getEcoNewsComment()));
        ecoNewsCommentService.deleteById(commentId, userVO);
        EcoNewsComment comment = verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void moderatorDeletesComment() {
        User user = ModelUtils.getUser();
        UserVO userVO = getUserVO();
        user.setRole(Role.ROLE_MODERATOR);
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId))
            .thenReturn(Optional.ofNullable(ModelUtils.getEcoNewsComment()));
        ecoNewsCommentService.deleteById(commentId, userVO);
        verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void adminDeletesComment() {
        User user = ModelUtils.getUser();
        UserVO userVO = getUserVO();
        user.setRole(Role.ROLE_ADMIN);
        Long commentId = 1L;
        when(ecoNewsCommentRepo.findById(commentId))
            .thenReturn(Optional.ofNullable(ModelUtils.getEcoNewsComment()));

        ecoNewsCommentService.deleteById(commentId, userVO);
        EcoNewsComment comment = verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void deleteCommentThatDoesntExistsThrowException() {
        User user = ModelUtils.getUser();
        UserVO userVO = getUserVO();
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> ecoNewsCommentService.deleteById(commentId, userVO));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());
    }

    @Test
    void deleteCommentUserHasNoPermissionThrowException() {
        User user = ModelUtils.getUser();

        User userToDelete = ModelUtils.getUser();
        UserVO userToDeleteVO = getUserVO();
        user.setId(2L);
        Long commentId = 1L;
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUser(user);

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));

        UserHasNoPermissionToAccessException noPermissionToAccessException =
            assertThrows(UserHasNoPermissionToAccessException.class,
                () -> ecoNewsCommentService.deleteById(commentId, userToDeleteVO));
        assertEquals(ErrorMessage.USER_HAS_NO_PERMISSION, noPermissionToAccessException.getMessage());
    }

    @Test
    void update() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        String newText = "new text";

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.ofNullable(ModelUtils.getEcoNewsComment()));

        ecoNewsCommentService.update(newText, commentId, userVO);
        verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void updateCommentThatDoesntExistsThrowException() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;
        String newText = "new text";

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class,
                () -> ecoNewsCommentService.update(newText, commentId, userVO));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());
    }

    @Test
    void updateCommentThatDoesntBelongsToUserThrowException() {
        User user = ModelUtils.getUser();
        UserVO userToUpdateVO = getUserVO();
        user.setId(2L);
        Long commentId = 1L;
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUser(user);
        String newText = "new text";

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> ecoNewsCommentService.update(newText, commentId,
                    userToUpdateVO));
        assertEquals(ErrorMessage.NOT_A_CURRENT_USER, badRequestException.getMessage());
    }

    @Test
    void likeComment() {
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        EcoNewsCommentVO ecoNewsCommentVO = ModelUtils.getEcoNewsCommentVO();
        User user = ModelUtils.getUser();
        UserVO userVO = getUserVO();
        userVO.setId(2L);
        Long commentId = 1L;

        ecoNewsComment.setUsersLiked(Collections.singleton(user));
        ecoNewsCommentVO.setUsersLiked(Collections.singleton(userVO));

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));
        when(modelMapper.map(ecoNewsComment, EcoNewsCommentVO.class)).thenReturn(ecoNewsCommentVO);
        when(modelMapper.map(ecoNewsCommentVO, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(modelMapper.map(ecoNewsComment.getUser(), UserVO.class)).thenReturn(userVO);

        ecoNewsCommentService.like(commentId, userVO);

        verify(ecoNewsService).likeComment(userVO, ecoNewsCommentVO);
    }

    @Test
    void unlikeComment() {
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        EcoNewsCommentVO ecoNewsCommentVO = ModelUtils.getEcoNewsCommentVO();
        User user = ModelUtils.getUser();
        UserVO userVO = getUserVO();
        Long commentId = 1L;

        ecoNewsComment.setUsersLiked(Collections.singleton(user));
        ecoNewsCommentVO.setUsersLiked(Collections.singleton(userVO));

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));
        when(modelMapper.map(ecoNewsComment, EcoNewsCommentVO.class)).thenReturn(ecoNewsCommentVO);
        when(modelMapper.map(ecoNewsCommentVO, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(modelMapper.map(ecoNewsComment.getUser(), UserVO.class)).thenReturn(userVO);

        ecoNewsCommentService.like(commentId, userVO);

        verify(ecoNewsService).unlikeComment(userVO, ecoNewsCommentVO);
    }

    @Test
    void likeCommentThatDoesntExistThrowException() {
        UserVO userVO = getUserVO();
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> ecoNewsCommentService.like(commentId, userVO));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());
    }

    @Test
    void countLikesCommentThatDoesntExistsThrowException() {
        AmountCommentLikesDto amountCommentLikesDto = AmountCommentLikesDto.builder()
            .id(1L)
            .amountLikes(2)
            .build();

        when(ecoNewsCommentRepo.findById(amountCommentLikesDto.getId())).thenReturn(Optional.empty());
        BadRequestException badRequestException =
            assertThrows(BadRequestException.class, () -> ecoNewsCommentService.countLikes(amountCommentLikesDto));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, badRequestException.getMessage());
    }

    @Test
    void countLikes() {
        AmountCommentLikesDto amountCommentLikesDto = AmountCommentLikesDto.builder()
            .id(1L)
            .amountLikes(2)
            .build();
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUsersLiked(new HashSet<>());

        when(ecoNewsCommentRepo.findById(amountCommentLikesDto.getId())).thenReturn(Optional.of(ecoNewsComment));
        doNothing().when(messagingTemplate).convertAndSend("/topic/"
            + amountCommentLikesDto.getId() + "/comment", amountCommentLikesDto);
        ecoNewsCommentService.countLikes(amountCommentLikesDto);
        verify(ecoNewsCommentRepo).findById(1L);
    }

    @Test
    void countRepliesCommentThatDoesntExistsThrowException() {
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException badRequestException =
            assertThrows(NotFoundException.class, () -> ecoNewsCommentService.countReplies(commentId));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, badRequestException.getMessage());
    }

    @Test
    void countReplies() {
        Long commentId = 1L;
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUsersLiked(new HashSet<>());

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));
        when(ecoNewsCommentRepo.countByParentCommentId(commentId)).thenReturn(0);

        assertEquals(0, ecoNewsCommentService.countReplies(commentId));
    }

    @Test
    void countCommentsThrowsExceptionTest() {
        when(ecoNewsRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> ecoNewsCommentService.countOfComments(1L));
    }

    @Test
    void getAllActiveComments() {
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUsersLiked(Collections.singleton(ModelUtils.getUser()));
        UserVO userVO = ModelUtils.getUserVO();

        List<EcoNewsComment> ecoNewsComments = Collections.singletonList(ecoNewsComment);
        EcoNewsCommentDto ecoNewsCommentDto = ModelUtils.getEcoNewsCommentDto();

        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNewsComment> page = new PageImpl<>(ecoNewsComments, pageRequest, ecoNewsComments.size());
        List<EcoNewsCommentDto> dtoList = Collections.singletonList(ecoNewsCommentDto);
        PageableDto<EcoNewsCommentDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);

        when(ecoNewsCommentRepo
            .findAllByParentCommentIsNullAndEcoNewsIdAndStatusNotOrderByCreatedDateDesc(pageRequest, 1L,
                CommentStatus.DELETED))
            .thenReturn(page);
        when(modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class)).thenReturn(ecoNewsCommentDto);
        when(ecoNewsCommentRepo.countByParentCommentId(ecoNewsCommentDto.getId())).thenReturn(10);

        PageableDto<EcoNewsCommentDto> actual = ecoNewsCommentService.getAllActiveComments(pageRequest, userVO, 1L);

        assertEquals(pageableDto, actual);
    }

    @Test
    void findAllActiveReplies() {
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUsersLiked(Collections.singleton(ModelUtils.getUser()));
        UserVO userVO = ModelUtils.getUserVO();

        List<EcoNewsComment> ecoNewsComments = Collections.singletonList(ecoNewsComment);
        EcoNewsCommentDto ecoNewsCommentDto = ModelUtils.getEcoNewsCommentDto();

        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNewsComment> page = new PageImpl<>(ecoNewsComments, pageRequest, ecoNewsComments.size());
        List<EcoNewsCommentDto> dtoList = Collections.singletonList(ecoNewsCommentDto);
        PageableDto<EcoNewsCommentDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);

        when(ecoNewsCommentRepo
            .findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageRequest, 1L, CommentStatus.DELETED))
            .thenReturn(page);

        when(modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class)).thenReturn(ecoNewsCommentDto);

        PageableDto<EcoNewsCommentDto> actual = ecoNewsCommentService.findAllActiveReplies(pageRequest, 1L, userVO);
        assertEquals(pageableDto, actual);
    }

    @Test
    void findAllActiveRepliesThrowException() {
        UserVO userVO = ModelUtils.getUserVO();
        List<EcoNewsComment> ecoNewsComments1 = List.of();
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<EcoNewsComment> page1 = new PageImpl<>(ecoNewsComments1, pageRequest, ecoNewsComments1.size());

        when(ecoNewsCommentRepo
            .findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageRequest, 11111L,
                CommentStatus.DELETED))
            .thenReturn(page1);

        assertThrows(NotFoundException.class,
            () -> ecoNewsCommentService.findAllActiveReplies(pageRequest, 11111L, userVO));

        verify(ecoNewsCommentRepo).findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(pageRequest,
            11111L, CommentStatus.DELETED);
    }

    @Test
    void searchUsersTest() {
        var user = getTagUser();
        var userTagDto = getUserTagDto();
        var userSearchDto = getUserSearchDto();

        when(userRepo.searchUsers("Test")).thenReturn(List.of(user));
        when(modelMapper.map(user, UserTagDto.class)).thenReturn(userTagDto);

        ecoNewsCommentService.searchUsers(userSearchDto);

        verify(userRepo).searchUsers("Test");
        verify(modelMapper).map(user, UserTagDto.class);
    }

    @Test
    void searchUsersWithNullSearchQueryTest() {
        var user = getTagUser();
        var userTagDto = getUserTagDto();
        var userSearchDto = getUserSearchDto();
        userSearchDto.setSearchQuery(null);

        when(userRepo.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(user, UserTagDto.class)).thenReturn(userTagDto);

        ecoNewsCommentService.searchUsers(userSearchDto);

        verify(userRepo).findAll();
        verify(modelMapper).map(user, UserTagDto.class);
    }

    @Test
    void likeOwnCommentThrowsException() {
        UserVO userVO = getUserVO();
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUsersLiked(new HashSet<>());

        when(ecoNewsCommentRepo.findById(anyLong())).thenReturn(Optional.of(ecoNewsComment));
        assertThrows(BadRequestException.class, () -> ecoNewsCommentService.like(1L, userVO));
    }

    @Test
    void countOfCommentsTest() {
        EcoNews ecoNews = new EcoNews();
        ecoNews.setId(1L);
        int commentCount = 5;

        when(ecoNewsRepo.findById(1L)).thenReturn(java.util.Optional.of(ecoNews));
        when(ecoNewsCommentRepo.countEcoNewsCommentByEcoNews(1L)).thenReturn(commentCount);

        int result = ecoNewsCommentService.countOfComments(1L);

        assertEquals(commentCount, result);
        verify(ecoNewsRepo).findById(1L);
        verify(ecoNewsCommentRepo).countEcoNewsCommentByEcoNews(1L);
    }
}

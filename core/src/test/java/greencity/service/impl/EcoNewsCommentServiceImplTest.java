package greencity.service.impl;

import greencity.ModelUtils;
import static greencity.ModelUtils.*;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoRequest;
import greencity.dto.econewscomment.AddEcoNewsCommentDtoResponse;
import greencity.dto.econewscomment.EcoNewsCommentDto;
import greencity.entity.EcoNews;
import greencity.entity.EcoNewsComment;
import greencity.entity.User;
import greencity.entity.enums.ROLE;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.AddEcoNewsCommentDtoResponseMapper;
import greencity.mapping.EcoNewsCommentDtoMapper;
import greencity.repository.EcoNewsCommentRepo;
import greencity.service.EcoNewsService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EcoNewsCommentServiceImplTest {
    @Mock
    private EcoNewsCommentRepo ecoNewsCommentRepo;
    @Mock
    private EcoNewsService ecoNewsService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private EcoNewsCommentServiceImpl ecoNewsCommentService;

    @BeforeEach
    public void setUp() {
        modelMapper.addConverter(new AddEcoNewsCommentDtoResponseMapper());
        modelMapper.addConverter(new EcoNewsCommentDtoMapper());
    }

    @Test
    void saveCommentWithNoParentCommentId() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNews);
        when(ecoNewsCommentRepo.save(any(EcoNewsComment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(modelMapper.map(any(EcoNewsComment.class), eq(AddEcoNewsCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddEcoNewsCommentDtoResponse());

        ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, getUser());
        verify(ecoNewsCommentRepo).save(any(EcoNewsComment.class));
    }

    @Test
    void saveCommentWithParentCommentId() {
        User user = getUser();
        EcoNews ecoNews = ModelUtils.getEcoNews();
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

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNews);
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(ecoNewsCommentRepo.save(any(EcoNewsComment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(modelMapper.map(any(EcoNewsComment.class), eq(AddEcoNewsCommentDtoResponse.class)))
            .thenReturn(ModelUtils.getAddEcoNewsCommentDtoResponse());
        when(ecoNewsCommentRepo.findById(anyLong())).thenReturn(Optional.ofNullable(ecoNewsCommentParent));

        ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, user);
        verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void saveCommentThatHaveReplyWithAnotherReplyThrowException() {
        User user = getUser();
        EcoNews ecoNews = ModelUtils.getEcoNews();
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

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNews);
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(ecoNewsCommentRepo.findById(anyLong())).thenReturn(Optional.ofNullable(ecoNewsCommentParent));

        BadRequestException badRequestException = assertThrows(BadRequestException.class,
            () -> ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, user));
        assertEquals(ErrorMessage.CANNOT_REPLY_THE_REPLY, badRequestException.getMessage());
    }

    @Test
    void saveCommentWithWrongParentIdThrowException() {
        EcoNews ecoNews = ModelUtils.getEcoNews();
        User user = ModelUtils.getUser();
        AddEcoNewsCommentDtoRequest addEcoNewsCommentDtoRequest = ModelUtils.getAddEcoNewsCommentDtoRequest();
        addEcoNewsCommentDtoRequest.setParentCommentId(2L);
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();

        when(ecoNewsService.findById(anyLong())).thenReturn(ecoNews);
        when(modelMapper.map(addEcoNewsCommentDtoRequest, EcoNewsComment.class)).thenReturn(ecoNewsComment);
        when(ecoNewsCommentRepo.findById(anyLong())).thenReturn(Optional.empty());

        BadRequestException badRequestException = assertThrows(BadRequestException.class,
            () -> ecoNewsCommentService.save(1L, addEcoNewsCommentDtoRequest, user));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, badRequestException.getMessage());
    }

    @Test
    void findAllComments() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Long ecoNewsId = 1L;
        EcoNewsComment ecoNewsComment = ModelUtils.getEcoNewsComment();
        ecoNewsComment.setUsersLiked(new HashSet<>());
        Page<EcoNewsComment> pages = new PageImpl<>(Collections.singletonList(ecoNewsComment), pageable, 1);
        EcoNewsCommentDto ecoNewsCommentDto = ModelUtils.getEcoNewsCommentDto();

        when(ecoNewsService.findById(1L)).thenReturn(ModelUtils.getEcoNews());
        when(ecoNewsCommentRepo.findAllByParentCommentIsNullAndEcoNewsIdOrderByCreatedDateDesc(pageable, ecoNewsId))
            .thenReturn(pages);
        when(modelMapper.map(ecoNewsComment, EcoNewsCommentDto.class)).thenReturn(ecoNewsCommentDto);
        when(ecoNewsCommentRepo.countByParentCommentId(any())).thenReturn(0);

        PageableDto<EcoNewsCommentDto> allComments =
            ecoNewsCommentService.findAllComments(pageable, getUser(), ecoNewsId);
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
        Long parentCommentId = 1L;
        EcoNewsComment ecoNewsCommentChild = getEcoNewsComment();
        ecoNewsCommentChild.setParentComment(getEcoNewsComment());
        ecoNewsCommentChild.setUsersLiked(new HashSet<>());
        Page<EcoNewsComment> pages = new PageImpl<>(Collections.singletonList(ecoNewsCommentChild), pageable, 1);

        when(ecoNewsCommentRepo.findAllByParentCommentIdOrderByCreatedDateDesc(pageable, parentCommentId))
            .thenReturn(pages);
        when(modelMapper.map(ecoNewsCommentChild, EcoNewsCommentDto.class)).thenReturn(getEcoNewsCommentDto());

        PageableDto<EcoNewsCommentDto> allReplies =
            ecoNewsCommentService.findAllReplies(pageable, parentCommentId, getUser());
        assertEquals(getEcoNewsCommentDto().getId(), allReplies.getPage().get(0).getId());
        assertEquals(4, allReplies.getTotalElements());
        assertEquals(1, allReplies.getCurrentPage());
        assertEquals(1, allReplies.getPage().size());
    }

    @Test
    void userDeletesOwnComment() {
        User user = ModelUtils.getUser();
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId))
            .thenReturn(Optional.ofNullable(ModelUtils.getEcoNewsComment()));

        ecoNewsCommentService.deleteById(commentId, user);
        EcoNewsComment comment = verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void moderatorDeletesComment() {
        User user = ModelUtils.getUser();
        user.setRole(ROLE.ROLE_MODERATOR);
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId))
            .thenReturn(Optional.ofNullable(ModelUtils.getEcoNewsComment()));

        ecoNewsCommentService.deleteById(commentId, user);
        verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void adminDeletesComment() {
        User user = ModelUtils.getUser();
        user.setRole(ROLE.ROLE_ADMIN);
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId))
            .thenReturn(Optional.ofNullable(ModelUtils.getEcoNewsComment()));

        ecoNewsCommentService.deleteById(commentId, user);
        EcoNewsComment comment = verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void deleteCommentThatDoesntExistsThrowException() {
        User user = ModelUtils.getUser();
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> ecoNewsCommentService.deleteById(commentId, user));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());
    }

    @Test
    void deleteCommentUserHasNoPermissionThrowException() {
        User user = ModelUtils.getUser();
        User userToDelete = ModelUtils.getUser();
        user.setId(2L);
        Long commentId = 1L;
        EcoNewsComment ecoNewsComment = getEcoNewsComment();
        ecoNewsComment.setUser(user);

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> ecoNewsCommentService.deleteById(commentId, userToDelete));
        assertEquals(ErrorMessage.USER_HAS_NO_PERMISSION, badRequestException.getMessage());
    }

    @Test
    void update() {
        User user = ModelUtils.getUser();
        Long commentId = 1L;
        String newText = "new text";

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.ofNullable(getEcoNewsComment()));

        ecoNewsCommentService.update(newText, commentId, user);
        verify(ecoNewsCommentRepo, times(1)).save(any(EcoNewsComment.class));
    }

    @Test
    void updateCommentThatDoesntExistsThrowException() {
        User user = ModelUtils.getUser();
        Long commentId = 1L;
        String newText = "new text";

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> ecoNewsCommentService.update(newText, commentId, user));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());
    }

    @Test
    void updateCommentThatDoesntBelongsToUserThrowException() {
        User user = ModelUtils.getUser();
        User userToUpdate = ModelUtils.getUser();
        user.setId(2L);
        Long commentId = 1L;
        EcoNewsComment ecoNewsComment = getEcoNewsComment();
        ecoNewsComment.setUser(user);
        String newText = "new text";

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class,
                () -> ecoNewsCommentService.update(newText, commentId, userToUpdate));
        assertEquals(ErrorMessage.NOT_A_CURRENT_USER, badRequestException.getMessage());
    }

    @Test
    void likeCommentThatDoesntExistThrowException() {
        User user = ModelUtils.getUser();
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        NotFoundException notFoundException =
            assertThrows(NotFoundException.class, () -> ecoNewsCommentService.like(commentId, user));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, notFoundException.getMessage());
    }

    @Test
    void countLikesCommentThatDoesntExistsThrowException() {
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class, () -> ecoNewsCommentService.countLikes(commentId));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, badRequestException.getMessage());
    }

    @Test
    void countLikes() {
        Long commentId = 1L;
        EcoNewsComment ecoNewsComment = getEcoNewsComment();
        ecoNewsComment.setUsersLiked(new HashSet<>());

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));

        assertEquals(0, ecoNewsCommentService.countLikes(commentId));
    }

    @Test
    void countRepliesCommentThatDoesntExistsThrowException() {
        Long commentId = 1L;

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.empty());

        BadRequestException badRequestException =
            assertThrows(BadRequestException.class, () -> ecoNewsCommentService.countReplies(commentId));
        assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, badRequestException.getMessage());
    }

    @Test
    void countReplies() {
        Long commentId = 1L;
        EcoNewsComment ecoNewsComment = getEcoNewsComment();
        ecoNewsComment.setUsersLiked(new HashSet<>());

        when(ecoNewsCommentRepo.findById(commentId)).thenReturn(Optional.of(ecoNewsComment));
        when(ecoNewsCommentRepo.countByParentCommentId(commentId)).thenReturn(0);

        assertEquals(0, ecoNewsCommentService.countReplies(commentId));
    }

    @Test
    void countComments() {
        Long ecoNewsId = 1L;

        when(ecoNewsCommentRepo.countOfComments(ecoNewsId)).thenReturn(0);

        assertEquals(0, ecoNewsCommentService.countOfComments(ecoNewsId));
    }
}
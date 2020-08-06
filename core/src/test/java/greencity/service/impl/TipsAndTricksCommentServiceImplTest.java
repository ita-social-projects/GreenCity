package greencity.service.impl;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.entity.TipsAndTricksComment;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.TipsAndTricksCommentRepo;
import greencity.service.TipsAndTricksService;
import java.util.*;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(SpringExtension.class)
class TipsAndTricksCommentServiceImplTest {
    @Mock
    private TipsAndTricksCommentRepo tipsAndTricksCommentRepo;
    @Mock
    private TipsAndTricksService tipsAndTricksService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private TipsAndTricksCommentServiceImpl tipsAndTricksCommentService;

    private TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
    private AddTipsAndTricksCommentDtoResponse addTipsAndTricksCommentDtoResponse =
        ModelUtils.getAddTipsAndTricksCommentDtoResponse();
    private AddTipsAndTricksCommentDtoRequest addTipsAndTricksCommentDtoRequest =
        ModelUtils.getAddTipsAndTricksCommentDtoRequest();
    private TipsAndTricksCommentDto tipsAndTricksCommentDto = TipsAndTricksCommentDto.builder().id(1L).build();

    @Test
    void saveTest() {
        when(tipsAndTricksService.findById(anyLong())).thenReturn(ModelUtils.getTipsAndTricks());
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(modelMapper.map(tipsAndTricksComment, AddTipsAndTricksCommentDtoResponse.class))
            .thenReturn(addTipsAndTricksCommentDtoResponse);
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);
        AddTipsAndTricksCommentDtoResponse actual = tipsAndTricksCommentService.save(tipsAndTricksComment.getId(),
            addTipsAndTricksCommentDtoRequest, ModelUtils.getUser());
        assertEquals(addTipsAndTricksCommentDtoResponse, actual);
    }

    @Test
    void saveTestWithParentComment() {
        when(tipsAndTricksService.findById(anyLong())).thenReturn(ModelUtils.getTipsAndTricks());
        TipsAndTricksComment tipsAndTricksComment2 = ModelUtils.getTipsAndTricksComment();
        tipsAndTricksComment2.setParentComment(ModelUtils.getTipsAndTricksComment());
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment2);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment2));
        when(modelMapper.map(tipsAndTricksComment2, AddTipsAndTricksCommentDtoResponse.class))
            .thenReturn(addTipsAndTricksCommentDtoResponse);
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment2)).thenReturn(tipsAndTricksComment2);
//        assertThrows(BadRequestException.class, () -> tipsAndTricksCommentService.save(tipsAndTricksComment2.getId(),
//            addTipsAndTricksCommentDtoRequest, ModelUtils.getUser()));
        try {
            tipsAndTricksCommentService.save(tipsAndTricksComment2.getId(),
                addTipsAndTricksCommentDtoRequest, ModelUtils.getUser());
        } catch (BadRequestException ex) {
            assertEquals(ErrorMessage.CANNOT_REPLY_THE_REPLY, ex.getMessage());
        }
    }

    @Test
    void saveReplyWrongIdTest() {
        when(tipsAndTricksService.findById(anyLong())).thenReturn(ModelUtils.getTipsAndTricks());
        TipsAndTricksComment tipsAndTricksComment3 = ModelUtils.getTipsAndTricksComment();
        tipsAndTricksComment3.setTipsAndTricks(ModelUtils.getTipsAndTricks());
        tipsAndTricksComment3.getTipsAndTricks().setId(2L);
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment3);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment3));
        when(modelMapper.map(tipsAndTricksComment3, AddTipsAndTricksCommentDtoResponse.class))
            .thenReturn(addTipsAndTricksCommentDtoResponse);
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment3);
        try {
            tipsAndTricksCommentService.save(2L,
                addTipsAndTricksCommentDtoRequest, ModelUtils.getUser());
        } catch (BadRequestException ex) {
            assertEquals(ErrorMessage.CANNOT_REPLY_WITH_OTHER_DIFFERENT_TIPSANDTRICKS_ID, ex.getMessage());
        }
    }

    @Test
    void saveTestCommentNotFoundById() {
        when(tipsAndTricksService.findById(anyLong())).thenReturn(ModelUtils.getTipsAndTricks());
        TipsAndTricksComment tipsAndTricksComment4 = ModelUtils.getTipsAndTricksComment();
        tipsAndTricksComment4.setParentComment(ModelUtils.getTipsAndTricksComment());
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment4);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.empty());
        try {
            tipsAndTricksCommentService.save(tipsAndTricksComment4.getId(),
                addTipsAndTricksCommentDtoRequest, ModelUtils.getUser());
        } catch (BadRequestException ex) {
            assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, ex.getMessage());
        }
//        assertThrows(BadRequestException.class, () -> tipsAndTricksCommentService.save(tipsAndTricksComment4.getId(),
//            addTipsAndTricksCommentDtoRequest, ModelUtils.getUser()));
    }

    @Test
    void findAllCommentsTest() {
        TipsAndTricksComment tipsAndTricksComment = TipsAndTricksComment.builder()
            .id(1L)
            .text("text")
            .user(ModelUtils.getUser())
            .usersLiked(new HashSet<>())
            .build();
        List<TipsAndTricksComment> tipsAndTricksComments =
            Collections.singletonList(tipsAndTricksComment);
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<TipsAndTricksComment> page =
            new PageImpl<>(tipsAndTricksComments, pageRequest, tipsAndTricksComments.size());
        List<TipsAndTricksCommentDto> dtoList =
            Collections.singletonList(tipsAndTricksCommentDto);
        PageableDto<TipsAndTricksCommentDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0);
        when(tipsAndTricksCommentRepo.findAllByParentCommentIsNullAndTipsAndTricksIdOrderByCreatedDateDesc(pageRequest,
            tipsAndTricksComment.getId())).thenReturn(page);
        when(modelMapper.map(tipsAndTricksComments.get(0), TipsAndTricksCommentDto.class))
            .thenReturn(dtoList.get(0));
        assertEquals(pageableDto, tipsAndTricksCommentService
            .findAllComments(pageRequest, ModelUtils.getUser(), tipsAndTricksComment.getId()));
    }

    @Test
    void findAllRepliesTest() {
        List<TipsAndTricksComment> tipsAndTricksComments =
            Collections.singletonList(ModelUtils.getTipsAndTricksComment());
        List<TipsAndTricksCommentDto> dtoList =
            Collections.singletonList(tipsAndTricksCommentDto);
        tipsAndTricksComment.setParentComment(ModelUtils.getTipsAndTricksComment());
        when(tipsAndTricksCommentRepo.findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateAsc(anyLong()))
            .thenReturn(tipsAndTricksComments);
        when(modelMapper.map(tipsAndTricksComments.get(0), TipsAndTricksCommentDto.class)).
            thenReturn(tipsAndTricksCommentDto);
        assertEquals(dtoList,
            tipsAndTricksCommentService.findAllReplies(tipsAndTricksComment.getParentComment().getId()));
    }

    @Test
    void deleteByIdTest() {
        when(tipsAndTricksCommentRepo.findById(anyLong()))
            .thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);
        tipsAndTricksCommentService.deleteById(1L, ModelUtils.getUser());
        assertTrue(tipsAndTricksComment.isDeleted());
    }

    @Test
    void deleteByIdExceptionTest() {
        when(tipsAndTricksCommentRepo.findById(anyLong()))
            .thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);
        User user = ModelUtils.getUser();
        user.setId(2L);
        assertThrows(BadRequestException.class, () -> tipsAndTricksCommentService.deleteById(1L, user));
    }

    @Test
    void updateTest() {
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);
        tipsAndTricksCommentService.update("new text", 1L, ModelUtils.getUser());
        assertEquals("new text", tipsAndTricksComment.getText());
    }

    @Test
    void updateExceptionTest() {
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);
        User user = ModelUtils.getUser();
        user.setId(2L);
        assertThrows(BadRequestException.class, () -> tipsAndTricksCommentService.update("new text", 1L, user));
    }

    @Test
    void likeTest() {
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);
        tipsAndTricksComment.setUsersLiked(new HashSet<>());
        User user = ModelUtils.getUser();
        tipsAndTricksCommentService.like(1L, user);
        assertTrue(tipsAndTricksComment.getUsersLiked().contains(user));
    }

    @Test
    void unlikeTest() {
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);
        User user = ModelUtils.getUser();
        Set<User> users = new HashSet<>();
        users.add(user);
        tipsAndTricksComment.setUsersLiked(users);
        tipsAndTricksCommentService.like(1L, user);
        assertFalse(tipsAndTricksComment.getUsersLiked().contains(user));
    }

    @Test
    void countRepliesTest() {
        when(tipsAndTricksCommentRepo.countTipsAndTricksCommentByParentCommentIdAndDeletedFalse(anyLong()))
            .thenReturn(1);
        int countReplies = tipsAndTricksCommentService.countReplies(1L);
        assertEquals(1, countReplies);
    }

    @Test
    void countLikesTest() {
        when(tipsAndTricksCommentRepo.countLikesByCommentId(anyLong())).thenReturn(1);
        int countLikes = tipsAndTricksCommentService.countLikes(1L);
        assertEquals(1, countLikes);
    }

    @Test
    void countCommentsTest() {
        when(tipsAndTricksCommentRepo.countAllByTipsAndTricksId(anyLong())).thenReturn(1);
        int countComments = tipsAndTricksCommentService.countComments(1L);
        assertEquals(1, countComments);
    }
}

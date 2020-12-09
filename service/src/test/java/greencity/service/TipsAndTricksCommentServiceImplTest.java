package greencity.service;

import greencity.ModelUtils;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.tipsandtricks.TipsAndTricksVO;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoRequest;
import greencity.dto.tipsandtrickscomment.AddTipsAndTricksCommentDtoResponse;
import greencity.dto.tipsandtrickscomment.TipsAndTricksCommentDto;
import greencity.dto.user.UserVO;
import greencity.dto.useraction.UserActionVO;
import greencity.entity.TipsAndTricks;
import greencity.entity.TipsAndTricksComment;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.TipsAndTricksCommentRepo;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TipsAndTricksCommentServiceImplTest {
    @Mock
    private TipsAndTricksCommentRepo tipsAndTricksCommentRepo;
    @Mock
    private TipsAndTricksService tipsAndTricksService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    UserActionService userActionService;
    @InjectMocks
    private TipsAndTricksCommentServiceImpl tipsAndTricksCommentService;

    private AddTipsAndTricksCommentDtoResponse addTipsAndTricksCommentDtoResponse =
        ModelUtils.getAddTipsAndTricksCommentDtoResponse();
    private AddTipsAndTricksCommentDtoRequest addTipsAndTricksCommentDtoRequest =
        ModelUtils.getAddTipsAndTricksCommentDtoRequest();
    private TipsAndTricksCommentDto tipsAndTricksCommentDto = TipsAndTricksCommentDto.builder().id(1L).build();

    @Test
    void saveTest() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        TipsAndTricksVO tipsAndTricksVO = ModelUtils.getTipsAndTricksVO();
        TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();

        when(tipsAndTricksService.findById(anyLong())).thenReturn(tipsAndTricksVO);
        when(modelMapper.map(tipsAndTricksVO, TipsAndTricks.class))
            .thenReturn(tipsAndTricks);
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(modelMapper.map(tipsAndTricksComment, AddTipsAndTricksCommentDtoResponse.class))
            .thenReturn(addTipsAndTricksCommentDtoResponse);
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);

        AddTipsAndTricksCommentDtoResponse actual = tipsAndTricksCommentService.save(tipsAndTricksComment.getId(),
            addTipsAndTricksCommentDtoRequest, ModelUtils.getUserVO());

        assertEquals(addTipsAndTricksCommentDtoResponse, actual);
    }

    @Test
    void saveTestWithParentComment() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        tipsAndTricksComment.setParentComment(ModelUtils.getTipsAndTricksComment());

        when(tipsAndTricksService.findById(anyLong())).thenReturn(ModelUtils.getTipsAndTricksVO());
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(modelMapper.map(tipsAndTricksComment, AddTipsAndTricksCommentDtoResponse.class))
            .thenReturn(addTipsAndTricksCommentDtoResponse);
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);

        try {
            tipsAndTricksCommentService.save(tipsAndTricksComment.getId(),
                addTipsAndTricksCommentDtoRequest, ModelUtils.getUserVO());
        } catch (BadRequestException ex) {
            assertEquals(ErrorMessage.CANNOT_REPLY_THE_REPLY, ex.getMessage());
        }
    }

    @Test
    void saveReplyWrongIdTest() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        TipsAndTricksVO tipsAndTricksVO = ModelUtils.getTipsAndTricksVO();
        TipsAndTricks tipsAndTricks = ModelUtils.getTipsAndTricks();

        tipsAndTricksComment.setTipsAndTricks(tipsAndTricks);
        tipsAndTricksComment.getTipsAndTricks().setId(2L);

        when(tipsAndTricksService.findById(anyLong())).thenReturn(tipsAndTricksVO);
        when(modelMapper.map(tipsAndTricksVO, TipsAndTricks.class))
            .thenReturn(tipsAndTricks);
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(modelMapper.map(tipsAndTricksComment, AddTipsAndTricksCommentDtoResponse.class))
            .thenReturn(addTipsAndTricksCommentDtoResponse);
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);

        try {
            tipsAndTricksCommentService.save(2L,
                addTipsAndTricksCommentDtoRequest, ModelUtils.getUserVO());
        } catch (BadRequestException ex) {
            assertEquals(ErrorMessage.CANNOT_REPLY_WITH_OTHER_DIFFERENT_TIPSANDTRICKS_ID, ex.getMessage());
        }
    }

    @Test
    void saveTestCommentNotFoundById() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        tipsAndTricksComment.setParentComment(ModelUtils.getTipsAndTricksComment());

        when(tipsAndTricksService.findById(anyLong())).thenReturn(ModelUtils.getTipsAndTricksVO());
        when(modelMapper.map(addTipsAndTricksCommentDtoRequest, TipsAndTricksComment.class))
            .thenReturn(tipsAndTricksComment);
        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.empty());

        try {
            tipsAndTricksCommentService.save(tipsAndTricksComment.getId(),
                addTipsAndTricksCommentDtoRequest, ModelUtils.getUserVO());
        } catch (BadRequestException ex) {
            assertEquals(ErrorMessage.COMMENT_NOT_FOUND_EXCEPTION, ex.getMessage());
        }
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
        List<TipsAndTricksCommentDto> dtoList = Collections.singletonList(tipsAndTricksCommentDto);
        PageableDto<TipsAndTricksCommentDto> pageableDto = new PageableDto<>(dtoList, dtoList.size(), 0, 1);

        when(tipsAndTricksCommentRepo.findAllByParentCommentIsNullAndTipsAndTricksIdOrderByCreatedDateDesc(pageRequest,
            tipsAndTricksComment.getId())).thenReturn(page);
        when(modelMapper.map(tipsAndTricksComments.get(0), TipsAndTricksCommentDto.class))
            .thenReturn(dtoList.get(0));

        assertEquals(pageableDto, tipsAndTricksCommentService
            .findAllComments(pageRequest, ModelUtils.getUserVO(), tipsAndTricksComment.getId()));
    }

    @Test
    void findAllRepliesTest() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        List<TipsAndTricksComment> tipsAndTricksComments =
            Collections.singletonList(ModelUtils.getTipsAndTricksComment());
        List<TipsAndTricksCommentDto> dtoList =
            Collections.singletonList(tipsAndTricksCommentDto);
        tipsAndTricksComment.setParentComment(ModelUtils.getTipsAndTricksComment());

        when(tipsAndTricksCommentRepo.findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateAsc(anyLong()))
            .thenReturn(tipsAndTricksComments);
        when(modelMapper.map(tipsAndTricksComments.get(0), TipsAndTricksCommentDto.class))
            .thenReturn(tipsAndTricksCommentDto);

        assertEquals(dtoList,
            tipsAndTricksCommentService.findAllReplies(tipsAndTricksComment.getParentComment().getId()));
    }

    @Test
    void deleteByIdTest() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        TipsAndTricksComment reply = TipsAndTricksComment.builder().id(2L).text("some text").build();
        tipsAndTricksComment.setComments(Collections.singletonList(reply));

        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);

        tipsAndTricksCommentService.deleteById(1L, ModelUtils.getUserVO());

        assertTrue(reply.isDeleted());
        assertTrue(tipsAndTricksComment.isDeleted());
    }

    @Test
    void deleteByIdExceptionTest() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setId(2L);

        when(tipsAndTricksCommentRepo.findById(anyLong()))
            .thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);

        assertThrows(BadRequestException.class, () -> tipsAndTricksCommentService.deleteById(1L, userVO));
    }

    @Test
    void updateTest() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();

        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);

        tipsAndTricksCommentService.update("new text", 1L, ModelUtils.getUserVO());

        assertEquals("new text", tipsAndTricksComment.getText());
    }

    @Test
    void updateExceptionTest() {
        TipsAndTricksComment tipsAndTricksComment = ModelUtils.getTipsAndTricksComment();
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setId(2L);

        when(tipsAndTricksCommentRepo.findById(anyLong())).thenReturn(Optional.of(tipsAndTricksComment));
        when(tipsAndTricksCommentRepo.save(tipsAndTricksComment)).thenReturn(tipsAndTricksComment);

        assertThrows(BadRequestException.class, () -> tipsAndTricksCommentService
            .update("new text", 1L, userVO));
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

    @Test
    void calculateTipsAndTricksComment() {
        UserVO userVO = ModelUtils.getUserVO();
        UserActionVO userActionVO = ModelUtils.getUserActionVO();
        when(userActionService.findUserActionByUserId(1L)).thenReturn(userActionVO);
        when(userActionService.updateUserActions(userActionVO)).thenReturn(userActionVO);
        tipsAndTricksCommentService.calculateTipsAndTricksComment(userVO);
    }
}

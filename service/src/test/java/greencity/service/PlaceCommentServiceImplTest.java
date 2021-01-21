package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentAdminDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.enums.UserStatus;
import greencity.repository.PlaceCommentRepo;
import java.util.Collections;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PlaceCommentServiceImplTest {
    @Mock
    private PlaceCommentRepo placeCommentRepo;
    @Mock
    private PlaceService placeService;
    @Mock
    private RestClient restClient;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PlaceCommentServiceImpl placeCommentService;

    @Test
    void findByIdTest() {
        Comment comment = ModelUtils.getComment();
        CommentReturnDto commentReturnDto = ModelUtils.getCommentReturnDto();
        when(placeCommentRepo.findById(1L))
            .thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentReturnDto.class))
            .thenReturn(commentReturnDto);
        CommentReturnDto result = placeCommentService.findById(1L);
        assertEquals(1, (long) result.getId());
    }

    @Test
    void deleteByIdTest() {
        Comment comment = ModelUtils.getComment();
        when(placeCommentRepo.findById(anyLong())).thenReturn(Optional.of(comment));
        doNothing().when(placeCommentRepo).delete(comment);
        placeCommentService.deleteById(1L);
        verify(placeCommentRepo, times(1)).delete(comment);
    }

    @Test
    void saveTest() {
        AddCommentDto addCommentDto = ModelUtils.getAddCommentDto();
        Comment comment = ModelUtils.getComment();
        when(placeService.findById(anyLong())).thenReturn(ModelUtils.getPlaceVO());
        UserVO userVO = ModelUtils.getUserVO();
        userVO.setUserStatus(UserStatus.ACTIVATED);
        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(addCommentDto, Comment.class)).thenReturn(comment);
        when(modelMapper.map(comment, CommentReturnDto.class))
            .thenReturn(ModelUtils.getCommentReturnDto());
        when(placeCommentRepo.save(any())).thenReturn(comment);
        placeCommentService.save(1L, addCommentDto, "email");
        verify(placeCommentRepo, times(1)).save(comment);
    }

    @Test
    void getAllCommentsTest() {
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<CommentAdminDto> commentAdminDtos = Collections.singletonList(new CommentAdminDto());
        List<Comment> list = Collections.singletonList(ModelUtils.getComment());
        Page<Comment> comments = new PageImpl<>(list, pageRequest, list.size());

        PageableDto<CommentAdminDto> result = new PageableDto<>(commentAdminDtos, commentAdminDtos.size(), 0, 1);
        when(modelMapper.map(list.get(0), CommentAdminDto.class)).thenReturn(commentAdminDtos.get(0));
        when(placeCommentRepo.findAll(pageRequest)).thenReturn(comments);

        assertEquals(result, placeCommentService.getAllComments(pageRequest));
    }
}

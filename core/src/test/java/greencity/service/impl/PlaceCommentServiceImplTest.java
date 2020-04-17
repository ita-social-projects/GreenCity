package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDto;
import greencity.dto.comment.CommentAdminDto;
import greencity.dto.comment.CommentReturnDto;
import greencity.entity.Comment;
import greencity.repository.PlaceCommentRepo;
import greencity.service.PlaceService;
import greencity.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.ArgumentMatchers.anyLong;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@RunWith(MockitoJUnitRunner.class)
public class PlaceCommentServiceImplTest {

    @Mock
    private PlaceCommentRepo placeCommentRepo;
    @Mock
    private PlaceService placeService;
    @Mock
    private UserService userService;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PlaceCommentServiceImpl placeCommentService;


    @Test
    public void findByIdTest() {
        Comment comment = ModelUtils.getComment();
        CommentReturnDto commentReturnDto = ModelUtils.getCommentReturnDto();
        when(placeCommentRepo.findById(1L))
            .thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentReturnDto.class)).
            thenReturn(commentReturnDto);
        CommentReturnDto result = placeCommentService.findById(1L);
        assertEquals(1, (long) result.getId());
    }


    @Test
    public void deleteByIdTest() {
        Comment comment = ModelUtils.getComment();
        when(placeCommentRepo.findById(anyLong())).thenReturn(Optional.of(comment));
        doNothing().when(placeCommentRepo).delete(comment);
        placeCommentService.deleteById(1L);
        verify(placeCommentRepo, times(1)).delete(comment);
    }

    @Test
    public void saveTest() {
        AddCommentDto addCommentDto = ModelUtils.getAddCommentDto();
        Comment comment = ModelUtils.getComment();
        comment.setUser(ModelUtils.getUser());
        comment.setPlace(ModelUtils.getPlace());
        when(placeService.findById(anyLong())).thenReturn(ModelUtils.getPlace());
        when(userService.findByEmail(anyString())).thenReturn(ModelUtils.getUser());
        when(modelMapper.map(addCommentDto, Comment.class)).thenReturn(comment);
        when(modelMapper.map(comment, CommentReturnDto.class))
            .thenReturn(ModelUtils.getCommentReturnDto());
        when(placeCommentRepo.save(any())).thenReturn(comment);
        placeCommentService.save(1L, addCommentDto, "email");
        verify(placeCommentRepo, times(1)).save(comment);
    }


    @Test
    public void getAllCommentsTest() {
        PageRequest pageRequest = new PageRequest(0, 2);
        List<CommentAdminDto> commentAdminDtos = Collections.singletonList(new CommentAdminDto());
        List<Comment> list = Collections.singletonList(ModelUtils.getComment());
        Page<Comment> comments = new PageImpl<Comment>(list, pageRequest, list.size());

        PageableDto<CommentAdminDto> result = new PageableDto<>(commentAdminDtos, commentAdminDtos.size(), 0);
        when(modelMapper.map(list.get(0), CommentAdminDto.class)).thenReturn(commentAdminDtos.get(0));
        when(placeCommentRepo.findAll(pageRequest)).thenReturn(comments);

        assertEquals(result, placeCommentService.getAllComments(pageRequest));
    }

}

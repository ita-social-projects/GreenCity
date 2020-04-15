package greencity.service.impl;

import greencity.ModelUtils;
import greencity.dto.comment.CommentReturnDto;
import greencity.entity.Comment;
import greencity.repository.PlaceCommentRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.modelmapper.ModelMapper;
import java.util.Optional;


import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlaceCommentServiceImplTest {

    @Mock
    private PlaceCommentRepo placeCommentRepo;
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
        assertEquals(1, (long)result.getId());
    }


    @Test
    public void deleteByIdTest(){
        Comment comment = ModelUtils.getComment();
        when(placeCommentRepo.findById(anyLong())).thenReturn(Optional.of(comment));
        doNothing().when(placeCommentRepo).delete(comment);
        placeCommentService.deleteById(1L);
        verify(placeCommentRepo, times(1)).delete(comment);
    }



}
package greencity.mapping;

import greencity.dto.comment.CommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import java.util.List;

import static greencity.ModelUtils.getComment;
import static greencity.ModelUtils.getCommentImage;
import static greencity.ModelUtils.getParentComment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentDtoMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CommentDtoMapper mapper;

    @Test
    void convertToDto() {
        Comment comment = getComment();
        User commentUser = comment.getUser();
        UserVO commentUserVO = mock(UserVO.class);
        String profilePicturePath = "profile picture path";

        when(modelMapper.map(commentUser, UserVO.class))
            .thenReturn(commentUserVO);
        when(commentUserVO.getProfilePicturePath())
            .thenReturn(profilePicturePath);

        CommentDto commentDto = mapper.convert(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getCreatedDate(), commentDto.getCreatedDate());
        assertEquals(comment.getModifiedDate(), commentDto.getModifiedDate());
        assertEquals(commentUser.getId(), commentDto.getAuthor().getId());
        assertEquals(commentUser.getName(), commentDto.getAuthor().getName());
        assertEquals(profilePicturePath, commentDto.getAuthor().getProfilePicturePath());
        assertNull(commentDto.getParentCommentId());
    }

    @Test
    void convertToDtoWithImages() {
        Comment comment = getComment();
        comment.setParentComment(getParentComment());
        comment.setAdditionalImages(List.of(getCommentImage()));
        User commentUser = comment.getUser();
        UserVO commentUserVO = mock(UserVO.class);
        String profilePicturePath = "profile picture path";

        when(modelMapper.map(commentUser, UserVO.class))
            .thenReturn(commentUserVO);
        when(commentUserVO.getProfilePicturePath())
            .thenReturn(profilePicturePath);

        CommentDto commentDto = mapper.convert(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertNotNull(commentDto.getAdditionalImages());
        assertEquals(comment.getAdditionalImages().getFirst().getLink(), commentDto.getAdditionalImages().getFirst());
        assertEquals(comment.getCreatedDate(), commentDto.getCreatedDate());
        assertEquals(comment.getModifiedDate(), commentDto.getModifiedDate());
        assertEquals(commentUser.getId(), commentDto.getAuthor().getId());
        assertEquals(commentUser.getName(), commentDto.getAuthor().getName());
        assertEquals(profilePicturePath, commentDto.getAuthor().getProfilePicturePath());
        assertEquals(comment.getParentComment().getId(), commentDto.getParentCommentId());
    }
}

package greencity.mapping;

import greencity.ModelUtils;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventCommentDtoMapperTest {

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    CommentDtoMapper commentDtoMapper;

    @Test
    void convertTest() {
        Comment comment = ModelUtils.getComment().setParentComment(new Comment().setId(2L));
        User commentUser = comment.getUser();
        UserVO commentUserVO = mock(UserVO.class);
        String profilePicturePath = "profile picture path";

        when(modelMapper.map(commentUser, UserVO.class))
                .thenReturn(commentUserVO);
        when(commentUserVO.getProfilePicturePath())
                .thenReturn(profilePicturePath);

        CommentDto actual = commentDtoMapper.convert(comment);

        assertNotNull(actual);
        assertEquals(comment.getId(), actual.getId(), "Comment ID mismatch");
        assertEquals(comment.getText(), actual.getText(), "Comment text mismatch");
        assertEquals(comment.getModifiedDate(), actual.getModifiedDate(), "Modified date mismatch");
        assertEquals(comment.getUsersLiked().size(), actual.getLikes(), "Likes count mismatch");
        assertEquals(comment.isCurrentUserLiked(), actual.isCurrentUserLiked(), "Current user liked status mismatch");
        assertEquals(comment.getParentComment().getId(), actual.getParentCommentId(), "Parent comment ID mismatch");
        assertEquals(commentUser.getId(), actual.getAuthor().getId());
        assertEquals(commentUser.getName(), actual.getAuthor().getName());
        assertEquals(profilePicturePath, actual.getAuthor().getProfilePicturePath());
    }
}

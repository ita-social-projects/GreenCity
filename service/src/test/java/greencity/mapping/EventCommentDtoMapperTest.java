package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.comment.CommentDto;
import greencity.entity.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventCommentDtoMapperTest {
    @InjectMocks
    private CommentDtoMapper commentDtoMapper;

    @Test
    void convertTest() {
        Comment comment = ModelUtils.getComment();
        CommentDto actual = commentDtoMapper.convert(comment);

        assertEquals(comment.getId(), actual.getId());
        assertEquals(comment.getText(), actual.getText());
        assertEquals(comment.getModifiedDate(), actual.getModifiedDate());
        assertEquals(comment.getComments().size(), actual.getReplies());
        assertEquals(comment.getUsersLiked().size(), actual.getLikes());
        assertEquals(comment.isCurrentUserLiked(), actual.isCurrentUserLiked());
        assertEquals(comment.getUser().getId(), actual.getAuthor().getId());
        assertEquals(comment.getUser().getName(), actual.getAuthor().getName());
        assertEquals(comment.getUser().getProfilePicturePath(), actual.getAuthor().getProfilePicturePath());
    }
}

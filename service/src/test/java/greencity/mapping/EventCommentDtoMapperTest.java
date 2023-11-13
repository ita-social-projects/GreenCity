package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.entity.event.EventComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventCommentDtoMapperTest {
    @InjectMocks
    private EventCommentDtoMapper eventCommentDtoMapper;

    @Test
    void convertTest() {
        EventComment eventComment = ModelUtils.getEventCommentWithReplies();
        EventCommentDto actual = eventCommentDtoMapper.convert(eventComment);

        assertEquals(eventComment.getId(), actual.getId());
        assertEquals(eventComment.getText(), actual.getText());
        assertEquals(eventComment.getModifiedDate(), actual.getModifiedDate());
        assertEquals(eventComment.getComments().size(), actual.getNumberOfReplies());
        assertEquals(eventComment.getUsersLiked().size(), actual.getLikes());
        assertEquals(eventComment.isCurrentUserLiked(), actual.isCurrentUserLiked());
        assertEquals(eventComment.getUser().getId(), actual.getAuthor().getId());
        assertEquals(eventComment.getUser().getName(), actual.getAuthor().getName());
        assertEquals(eventComment.getUser().getProfilePicturePath(), actual.getAuthor().getUserProfilePicturePath());
    }
}

package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.event.EventComment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventCommentVOMapperTest {
    @InjectMocks
    EventCommentVOMapper eventCommentVOMapper;

    @Test
    void convertTest() {
        EventComment eventComment = ModelUtils.getEventCommentWithReplies();
        EventCommentVO actual = eventCommentVOMapper.convert(eventComment);
        Set<UserVO> usersLiked = eventComment.getUsersLiked().stream()
            .map(user -> UserVO.builder()
                .id(user.getId())
                .build())
            .collect(Collectors.toSet());

        assertEquals(eventComment.getId(), actual.getId());
        assertEquals(eventComment.getText(), actual.getText());
        assertEquals(eventComment.getCreatedDate(), actual.getCreatedDate());
        assertEquals(eventComment.getParentComment().getId(), actual.getParentComment().getId());
        assertEquals(eventComment.getUser().getId(), actual.getUser().getId());
        assertEquals(eventComment.getUser().getName(), actual.getUser().getName());
        assertEquals(eventComment.getUser().getRole().name(), actual.getUser().getRole().name());
        assertEquals(eventComment.getEvent().getId(), actual.getEvent().getId());
        assertEquals(eventComment.getStatus().toString(), actual.getStatus());
        assertEquals(eventComment.getText(), actual.getText());
        assertEquals(eventComment.isCurrentUserLiked(), actual.isCurrentUserLiked());
        assertEquals(usersLiked, actual.getUsersLiked());
    }
}

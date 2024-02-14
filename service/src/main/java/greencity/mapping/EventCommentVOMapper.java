package greencity.mapping;

import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.event.EventComment;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link EventComment} into
 * {@link EventCommentVO}.
 */

public class EventCommentVOMapper extends AbstractConverter<EventComment, EventCommentVO> {
    @Override
    protected EventCommentVO convert(EventComment eventComment) {
        return EventCommentVO.builder()
            .id(eventComment.getId())
            .text(eventComment.getText())
            .createdDate(eventComment.getCreatedDate())
            .modifiedDate(eventComment.getModifiedDate())
            .parentComment(EventCommentVO.builder()
                .id(eventComment.getParentComment().getId())
                .build())
            .user(UserVO.builder()
                .id(eventComment.getUser().getId())
                .role(eventComment.getUser().getRole())
                .name(eventComment.getUser().getName())
                .build())
            .event(EventVO.builder()
                .id(eventComment.getEvent().getId())
                .build())
            .currentUserLiked(eventComment.isCurrentUserLiked())
            .usersLiked(eventComment.getUsersLiked().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()))
            .status(eventComment.getStatus().toString())
            .build();
    }
}

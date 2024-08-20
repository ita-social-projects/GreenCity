package greencity.mapping;

import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.event.EventComment;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link EventComment} into
 * {@link EventCommentVO}.
 */

@Component
public class EventCommentVOMapper extends AbstractConverter<EventComment, EventCommentVO> {
    @Override
    public EventCommentVO convert(EventComment eventComment) {
        return EventCommentVO.builder()
            .id(eventComment.getId())
            .text(eventComment.getText())
            .createdDate(eventComment.getCreatedDate())
            .modifiedDate(eventComment.getModifiedDate())
            .parentComment(eventComment.getParentComment() != null ?
                EventCommentVO.builder()
                    .id(eventComment.getParentComment().getId())
                .build() : null)
            .user(UserVO.builder()
                .id(eventComment.getUser().getId())
                .role(eventComment.getUser().getRole())
                .name(eventComment.getUser().getName())
                .build())
            .event(EventVO.builder()
                .id(eventComment.getEvent().getId())
                .build())
            .currentUserLiked(eventComment.isCurrentUserLiked())
            .usersLiked(eventComment.getUsersLiked() != null ?
                eventComment.getUsersLiked().stream()
                    .map(user -> UserVO.builder()
                        .id(user.getId())
                        .build())
                    .collect(Collectors.toSet()) : null)
            .status(eventComment.getStatus() != null ?
                    eventComment.getStatus().toString() : null)
            .build();
    }
}

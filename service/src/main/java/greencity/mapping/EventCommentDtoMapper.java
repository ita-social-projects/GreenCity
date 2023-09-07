package greencity.mapping;

import greencity.dto.eventcomment.EventCommentAuthorDto;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.entity.event.EventComment;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EventComment} into
 * {@link EventCommentDto}.
 */

@Component
public class EventCommentDtoMapper extends AbstractConverter<EventComment, EventCommentDto> {
    @Override
    protected EventCommentDto convert(EventComment eventComment) {
        return EventCommentDto.builder()
            .id(eventComment.getId())
            .modifiedDate(eventComment.getModifiedDate())
            .author(EventCommentAuthorDto.builder()
                .id(eventComment.getUser().getId())
                .name(eventComment.getUser().getName())
                .userProfilePicturePath(eventComment.getUser().getProfilePicturePath())
                .build())
            .text(eventComment.getText())
            .numberOfReplies(eventComment.getComments().size())
            .likes(eventComment.getUsersLiked().size())
            .currentUserLiked(eventComment.isCurrentUserLiked())
            .status(eventComment.getStatus().toString())
            .build();
    }
}

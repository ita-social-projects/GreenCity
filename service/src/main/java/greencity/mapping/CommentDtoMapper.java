package greencity.mapping;

import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.comment.CommentDto;
import greencity.entity.Comment;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Comment} into
 * {@link CommentDto}.
 */

@Component
public class CommentDtoMapper extends AbstractConverter<Comment, CommentDto> {
    @Override
    protected CommentDto convert(Comment comment) {
        return CommentDto.builder()
            .id(comment.getId())
            .modifiedDate(comment.getModifiedDate())
            .author(CommentAuthorDto.builder()
                .id(comment.getUser().getId())
                .name(comment.getUser().getName())
                .profilePicturePath(comment.getUser().getProfilePicturePath())
                .build())
            .text(comment.getText())
            .replies(comment.getComments().size())
            .likes(comment.getUsersLiked().size())
            .currentUserLiked(comment.isCurrentUserLiked())
            .status(comment.getStatus().toString())
            .build();
    }
}

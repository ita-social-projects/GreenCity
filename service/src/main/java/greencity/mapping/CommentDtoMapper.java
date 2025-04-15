package greencity.mapping;

import greencity.dto.comment.CommentAuthorDto;
import greencity.dto.comment.CommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.CommentImages;
import greencity.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Comment} into
 * {@link CommentDto}.
 */
@Component
@RequiredArgsConstructor
public class CommentDtoMapper extends AbstractConverter<Comment, CommentDto> {

    private final ModelMapper modelMapper;

    /**
     * Method for converting {@link Comment} into {@link CommentDto}.
     *
     * @param comment object to convert.
     * @return converted object.
     */
    @Override
    protected CommentDto convert(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setCreatedDate(comment.getCreatedDate());
        commentDto.setModifiedDate(comment.getModifiedDate());
        if (comment.getParentComment() != null) {
            commentDto.setParentCommentId(comment.getParentComment().getId());
        }
        commentDto.setText(comment.getText());
        commentDto.setStatus(commentDto.getStatus());
        if (comment.getAdditionalImages() != null) {
            commentDto.setAdditionalImages(comment.getAdditionalImages().stream().map(CommentImages::getLink).toList());
        }
        commentDto.setCurrentUserLiked(comment.isCurrentUserLiked());
        commentDto.setCurrentUserDisliked(comment.isCurrentUserDisliked());
        commentDto.setLikes(comment.getUsersLiked().size());
        commentDto.setDislikes(comment.getUsersDisliked().size());
        User commentUser = comment.getUser();
        UserVO commentUserVO = modelMapper.map(commentUser, UserVO.class);

        commentDto.setAuthor(
            CommentAuthorDto.builder()
                .id(commentUser.getId())
                .name(commentUserVO.getName())
                .profilePicturePath(commentUserVO.getProfilePicturePath()).build());
        return commentDto;
    }
}

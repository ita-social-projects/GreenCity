package greencity.mapping;

import greencity.dto.comment.CommentVO;
import greencity.dto.user.UserVO;
import greencity.entity.Comment;
import greencity.entity.User;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link Comment} into
 * {@link CommentVO}.
 */

@Component
public class CommentVOMapper extends AbstractConverter<Comment, CommentVO> {

    private final ModelMapper modelMapper;

    @Lazy
    public CommentVOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CommentVO convert(Comment comment) {
        User commentUser = comment.getUser();
        UserVO commentUserVO = modelMapper.map(commentUser, UserVO.class);

        return CommentVO.builder()
            .id(comment.getId())
            .text(comment.getText())
            .articleType(comment.getArticleType().toString())
            .articleId(comment.getArticleId() != null ? comment.getArticleId() : null)
            .createdDate(comment.getCreatedDate())
            .modifiedDate(comment.getModifiedDate())
            .parentComment(comment.getParentComment() != null ? CommentVO.builder()
                .id(comment.getParentComment().getId())
                .build() : null)
            .user(UserVO.builder()
                .id(commentUser.getId())
                .role(commentUserVO.getRole())
                .name(commentUser.getName())
                .build())
            .currentUserLiked(comment.isCurrentUserLiked())
            .usersLiked(comment.getUsersLiked() != null ? comment.getUsersLiked().stream()
                .map(user -> UserVO.builder()
                    .id(user.getId())
                    .build())
                .collect(Collectors.toSet()) : null)
            .status(comment.getStatus() != null ? comment.getStatus().toString() : null)
            .build();
    }
}

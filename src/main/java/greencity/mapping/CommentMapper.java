package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.comment.AddCommentDto;
import greencity.entity.Comment;
import greencity.exception.NotImplementedMethodException;

public class CommentMapper implements Mapper<AddCommentDto, Comment> {
    @Override
    public AddCommentDto convertToEntity(Comment dto) {
        return null;
    }

    @Override
    public Comment convertToDto(AddCommentDto entity) {
        throw new NotImplementedMethodException(ErrorMessage.NOT_IMPLEMENTED_METHOD);
    }
}

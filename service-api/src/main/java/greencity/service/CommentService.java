package greencity.service;

import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentVO;
import greencity.dto.user.UserVO;
import greencity.enums.ArticleType;

public interface CommentService {
    /**
     * Method to save {@link CommentVO}.
     *
     * @param articleType               type {@link ArticleType} of article to which we save
     *                                  comment.
     * @param articleId                 id of article to which we save
     *                                  comment.
     * @param addCommentDtoRequest dto with {@link CommentVO} text,
     *                                  parentCommentId.
     * @param userVO                      {@link UserVO} that saves the comment.
     * @return {@link AddCommentDtoResponse} instance.
     * @author Dmytro Fedotov
     */
    AddCommentDtoResponse save(ArticleType articleType, Long articleId, AddCommentDtoRequest addCommentDtoRequest,
                               UserVO userVO);


    /**
     * Method to get certain comment specified by commentId.
     *
     * @param id specifies {@link CommentDto} to which we search for comments
     * @return comment to certain article specified by commentId.
     */
    CommentDto findCommentById(Long id, UserVO userVO);
}

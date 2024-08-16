package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.comment.AddCommentDtoRequest;
import greencity.dto.comment.AddCommentDtoResponse;
import greencity.dto.comment.CommentDto;
import greencity.dto.comment.CommentVO;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.eventcomment.EventCommentVO;
import greencity.dto.user.UserVO;
import greencity.enums.ArticleType;
import org.springframework.data.domain.Pageable;

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


    /**
     * Method to get all not deleted replies for to certain {@link CommentVO}
     * specified by id.
     *
     * @param parentCommentId to specify {@link CommentVO}.
     * @param userVO            {@link UserVO} that want to get replies.
     * @return replies for comment
     */
    PageableDto<CommentDto> findAllActiveReplies(Pageable pageable, Long parentCommentId, UserVO userVO);

    /**
     * Method to count not deleted comments for certain article.
     *
     * @param articleId to specify article
     * @param type to specify {@link ArticleType}
     * @return amount of comments
     */
    int countComments(ArticleType type, Long articleId);

    /**
     * Method to count not deleted replies for to certain {@link CommentVO}
     * specified by id.
     *
     * @param parentCommentId to specify {@link CommentVO}.
     * @return amount of replies.
     */
    int countAllActiveReplies(Long parentCommentId);

    /**
     * Method to get all active comments for article specified by articleId.
     *
     * @param pageable page of article.
     * @param articleId  specifies article to which we search comments
     * @return all active comments to certain article specified by articleId.
     */
    PageableDto<CommentDto> getAllActiveComments(Pageable pageable, UserVO userVO, Long articleId, ArticleType type);
}

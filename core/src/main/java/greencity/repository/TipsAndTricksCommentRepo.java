package greencity.repository;

import greencity.entity.TipsAndTricksComment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TipsAndTricksCommentRepo extends JpaRepository<TipsAndTricksComment, Long> {

    /**
     * Method returns all {@link TipsAndTricksComment} by page.
     *
     * @param pageable        page of news.
     * @param tipsAndTricksId id of {@link greencity.entity.TipsAndTricks} for which comments we search.
     * @return all {@link TipsAndTricksComment} by page.
     */
    @Query("SELECT tc " +
        "FROM TipsAndTricksComment tc " +
        "WHERE tc.parentComment IS NULL " +
        "AND tc.tipsAndTricks.id = ?1 " +
        "AND tc.deleted=false " +
        "ORDER BY tc.createdDate DESC")
    Page<TipsAndTricksComment> findAllByParentCommentIsNullAndTipsAndTricksIdOrderByCreatedDateDesc(Pageable pageable,
                                                                                                    Long tipsAndTricksId);

    /**
     * Method returns all replies to comment, specified by parentCommentId.
     *
     * @param parentCommentId id of comment, replies to which we get.
     * @return all replies to comment, specified by parentCommentId.
     */
    List<TipsAndTricksComment> findAllByParentCommentIdAndDeletedFalseOrderByCreatedDateAsc(Long parentCommentId);

    /**
     * Method returns count of comments for TipsAndTrick, specified by tipAndTricksId.
     *
     * @param tipAndTricksId id of TipsAndTrick, count of comments to which we get.
     * @return count of comments to TipsAndTrick, specified by tipAndTricksId.
     */
    @Query("SELECT count(tc) " +
        "FROM TipsAndTricksComment tc " +
        "WHERE tc.tipsAndTricks.id = ?1 " +
        "AND tc.deleted=false")
    int countAllByTipsAndTricksId(Long tipAndTricksId);

    /**
     * Method returns count of replies to comment, specified by parentCommentId.
     *
     * @param parentCommentId id of comment, count of replies to which we get.
     * @return count of replies to comment, specified by parentCommentId.
     */
    int countTipsAndTricksCommentByParentCommentIdAndDeletedFalse(Long parentCommentId);

    /**
     * Method returns count of likes to comment, specified by commentId.
     *
     * @param commentId id of comment, count of likes to which we get.
     * @return count of likes to comment, specified by commentId.
     */
    @Query("SELECT tl.usersLiked.size " +
        "FROM TipsAndTricksComment tl " +
        "WHERE tl.id = ?1 AND tl.deleted=false")
    int countLikesByCommentId(Long commentId);

}
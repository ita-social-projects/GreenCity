package greencity.repository;

import greencity.entity.EcoNewsComment;
import greencity.enums.CommentStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsCommentRepo extends JpaRepository<EcoNewsComment, Long> {
    /**
     * Method returns all {@link EcoNewsComment} by page, parentCommentId and
     * statuses.
     *
     * @param pageable        page of news.
     * @param parentCommentId id of comment, replies to which we get.
     * @param status          statuses of comment.
     * @return all replies to comment, specified by parentCommentId, page and
     *         statuses.
     * @author Ilia Rozhko
     */
    Page<EcoNewsComment> findAllByParentCommentIdAndStatusInOrderByCreatedDateDesc(
        Pageable pageable, Long parentCommentId, List<CommentStatus> status);

    /**
     * Method returns count of replies to comment, specified by parentCommentId.
     *
     * @param parentCommentId id of comment, count of replies to which we get.
     * @return count of replies to comment, specified by parentCommentId.
     */
    @Query("SELECT count(ec) from EcoNewsComment ec where ec.parentComment.id = ?1 AND ec.status <> 'DELETED'")
    int countByParentCommentId(Long parentCommentId);

    /**
     * The method returns the count of not deleted comments, specified by.
     *
     * @param ecoNewsId {@link Long} - id of ecoNews.
     * @return count of comments, specified by {@link greencity.entity.EcoNews}
     */
    @Query(value = "SELECT count(ec.id) FROM econews_comment ec "
        + "JOIN eco_news en ON en.id = ec.eco_news_id "
        + "WHERE en.id = :ecoNewsId and ec.status <>'DELETED'", nativeQuery = true)
    int countEcoNewsCommentByEcoNews(Long ecoNewsId);

    /**
     * Method returns {@link EcoNewsComment} by page, id of eco news and statuses.
     *
     * @param pageable  page.
     * @param ecoNewsId id of {@link greencity.entity.EcoNews} for which comments we
     *                  search.
     * @param status    statuses of comments.
     * @return {@link EcoNewsComment} by page.
     * @author Ilia Rozhko
     */
    Page<EcoNewsComment> findAllByEcoNewsIdAndParentCommentIsNullAndStatusInOrderByCreatedDateDesc(
        Pageable pageable, Long ecoNewsId, List<CommentStatus> status);
}

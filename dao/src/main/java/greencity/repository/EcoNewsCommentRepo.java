package greencity.repository;

import greencity.entity.EcoNewsComment;
import greencity.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsCommentRepo extends JpaRepository<EcoNewsComment, Long> {
    /**
     * Method returns all {@link EcoNewsComment} by page.
     *
     * @param pageable  page of news.
     * @param ecoNewsId id of {@link greencity.entity.EcoNews} for which comments we
     *                  search.
     * @return all {@link EcoNewsComment} by page.
     */
    Page<EcoNewsComment> findAllByParentCommentIsNullAndEcoNewsIdOrderByCreatedDateDesc(Pageable pageable,
        Long ecoNewsId);

    /**
     * Method returns all replies to comment, specified by parentCommentId and by
     * page.
     *
     * @param pageable        page of news.
     * @param parentCommentId id of comment, replies to which we get.
     * @return all replies to comment, specified by parentCommentId and page.
     */
    Page<EcoNewsComment> findAllByParentCommentIdOrderByCreatedDateDesc(Pageable pageable,
        Long parentCommentId);

    /**
     * Method returns count of replies to comment, specified by parentCommentId.
     *
     * @param parentCommentId id of comment, count of replies to which we get.
     * @return count of replies to comment, specified by parentCommentId.
     */
    @Query("SELECT count(ec) from EcoNewsComment ec where ec.parentComment.id = ?1 AND ec.status <> 'DELETED'")
    int countByParentCommentId(Long parentCommentId);

    /**
     * The method returns the count of not deleted comments, specified by ecoNewsId.
     *
     * @return count of comments, specified by {@link greencity.entity.EcoNews}.
     */
    @Query("SELECT count(ec) FROM EcoNewsComment ec "
        + "WHERE ec.parentComment IS NULL AND ec.ecoNews.id = ?1 AND ec.status <> 'DELETED'")
    int countOfComments(Long ecoNewsId);

    /**
     * The method returns the count of not deleted comments, specified by.
     *
     * @param ecoNewsId {@link Long} - id of ecoNews.
     *
     * @return count of comments, specified by {@link greencity.entity.EcoNews}
     */
    @Query(value = "SELECT count(ec.id) FROM econews_comment ec "
        + "JOIN eco_news en ON en.id = ec.eco_news_id "
        + "WHERE en.id = :ecoNewsId and ec.status <>'DELETED'", nativeQuery = true)
    int countEcoNewsCommentByEcoNews(Long ecoNewsId);

    /**
     * Method returns all {@link EcoNewsComment} by page.
     *
     * @param pageable  page of news.
     * @param ecoNewsId id of {@link greencity.entity.EcoNews} for which comments we
     *                  search.
     * @return all active {@link EcoNewsComment} by page.
     * @author Dovganyuk Taras
     */
    Page<EcoNewsComment> findAllByParentCommentIsNullAndEcoNewsIdAndStatusNotOrderByCreatedDateDesc(
        Pageable pageable, Long ecoNewsId, CommentStatus status);

    /**
     * Method returns all {@link EcoNewsComment} by page.
     *
     * @param pageable        page of news.
     * @param parentCommentId id of comment, replies to which we get.
     * @return all replies to comment, specified by parentCommentId and page.
     * @author Dovganyuk Taras
     */
    Page<EcoNewsComment> findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(Pageable pageable,
        Long parentCommentId, CommentStatus status);
}

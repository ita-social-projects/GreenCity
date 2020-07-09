package greencity.repository;

import greencity.entity.EcoNewsComment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EcoNewsCommentRepo extends JpaRepository<EcoNewsComment, Long> {
    /**
     * Method returns all {@link EcoNewsComment} by page.
     * @param pageable page of news.
     * @param ecoNewsId id of {@link greencity.entity.EcoNews} for which comments we search.
     * @return all {@link EcoNewsComment} by page.
     */
    Page<EcoNewsComment> findAllByParentCommentIsNullAndEcoNewsIdOrderByCreatedDateAsc(Pageable pageable,
                                                                                       Long ecoNewsId);

    /**
     * Method returns all replies to comment, specified by parentCommentId.
     *
     * @param parentCommentId id of comment, replies to which we get.
     * @return all replies to comment, specified by parentCommentId.
     */
    List<EcoNewsComment> findAllByParentCommentIdOrderByCreatedDateAsc(Long parentCommentId);

    /**
     * Method returns count of replies to comment, specified by parentCommentId.
     *
     * @param parentCommentId id of comment, count of replies to which we get.
     * @return count of replies to comment, specified by parentCommentId.
     */
    @Query("SELECT count(ec) from EcoNewsComment ec where ec.parentComment.id = ?1")
    int countByParentCommentId(Long parentCommentId);
}

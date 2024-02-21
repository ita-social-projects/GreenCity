package greencity.repository;

import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    /**
     * The method returns the count of not deleted comments, specified by.
     *
     * @param eventId {@link Long} - id of event.
     *
     * @return count of not deleted comments, specified by {@link Event}
     */
    @Query(value = "select count(ec.id) from events_comment ec"
        + " join events event on event.id = ec.event_id"
        + " where event.id = :eventId and ec.status <>'DELETED'", nativeQuery = true)
    int countNotDeletedEventCommentsByEvent(Long eventId);

    /**
     * The method returns not deleted comment {@link EventComment}, specified by id.
     *
     * @param id id of {@link EventComment} parent comment
     * @return not deleted comment by it id
     */
    Optional<EventComment> findByIdAndStatusNot(Long id, CommentStatus status);

    /**
     * Method returns all {@link EventComment} by page.
     *
     * @param pageable page of news.
     * @param eventId  id of {@link Event} for which comments we search.
     * @return all active {@link EventComment} by page.
     */
    Page<EventComment> findAllByParentCommentIdIsNullAndEventIdAndStatusNotOrderByCreatedDateDesc(Pageable pageable,
        Long eventId, CommentStatus status);

    /**
     * Method returns all {@link EventComment} not deleted replies to the comment by
     * page.
     *
     * @param pageable        page of news.
     * @param parentCommentId id of {@link EventComment} parent comment
     * @return all replies to comment, specified by parentCommentId and page.
     */
    Page<EventComment> findAllByParentCommentIdAndStatusNotOrderByCreatedDateDesc(Pageable pageable,
        Long parentCommentId, CommentStatus status);

    /**
     * Method returns the count of not deleted replies to the comment, specified by
     * parent comment {@link EventComment} id.
     *
     * @param parentCommentId id of {@link EventComment} parent comment
     * @return count of comments, specified by {@link EventComment}
     */
    int countByParentCommentIdAndStatusNot(Long parentCommentId, CommentStatus status);
}

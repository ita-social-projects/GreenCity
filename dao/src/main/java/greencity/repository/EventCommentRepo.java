package greencity.repository;

import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventCommentRepo extends JpaRepository<EventComment, Long> {
    /**
     * The method returns the count of not deleted comments, specified by
     * {@link Event}.
     *
     * @return count of comments, specified by {@link Event}
     */
    @Query(value = "select count(ec.id) from events_comment ec"
        + " join events event on event.id = ec.event_id"
        + " where event.id = :event", nativeQuery = true)
    int countEventCommentsByEvent(Event event);

    /**
     * Method returns all {@link EventComment} by page.
     *
     * @param pageable page of news.
     * @param eventId  id of {@link Event} for which comments we search.
     * @return all active {@link EventComment} by page.
     */
    Page<EventComment> findAllByEventIdOrderByCreatedDateDesc(Pageable pageable, Long eventId);
}

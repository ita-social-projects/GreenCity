package greencity.repository;

import greencity.entity.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    /**
     * Method for getting all events.
     *
     * @return list of {@link Event} instances.
     */
    Page<Event> findAllByOrderByIdDesc(Pageable page);

    /**
     * Method returns {@link Event} by search query and page.
     *
     * @param paging {@link Pageable}.
     * @param query  query to search.
     * @return list of {@link Event}.
     */
    @Query(nativeQuery = true,
        value = "SELECT e.id, e.title, e.descripton, e.author_id, u.name, tt.name"
            + "FROM events e "
            + "JOIN users u on u.id = e.author_id "
            + "JOIN events_tags ent on e.id = ent.event_id "
            + "JOIN tag_translations tt on tt.tag_id = ent.tags_id "
            + "WHERE concat(e.id,'') like :query or "
            + "    lower(e.title) like lower(concat('%', :query, '%')) or "
            + "    lower(e.description) like lower(concat('%', :query, '%')) or "
            + "    lower(u.name) like lower(concat('%', :query, '%')) or "
            + "    lower(tt.name) like lower(concat('%', :query, '%'))")
    Page<Event> searchEventsBy(Pageable paging, String query);

    /**
     * Remove event datesLocations.
     *
     * @param eventId {@link Long}.
     */
    @Modifying
    @Query(value = "DELETE FROM events_dates_locations WHERE event_id = :eventId", nativeQuery = true)
    void deleteEventDateLocationsByEventId(Long eventId);

    /**
     * Remove event additional images.
     *
     * @param eventId {@link Long}.
     */
    @Modifying
    @Query(value = "DELETE FROM events_images WHERE event_id = :eventId", nativeQuery = true)
    void deleteEventAdditionalImagesByEventId(Long eventId);
}

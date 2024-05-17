package greencity.repository;

import greencity.entity.User;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EventRepo extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    /**
     * Method for getting all events.
     *
     * @return list of {@link Event} instances.
     */
    Page<Event> findAllByOrderByIdDesc(Pageable page);

    /**
     * Method for getting all events sorted by start date.
     *
     * @return list of {@link Event} of future events sorted by start date in
     *         ascending order, followed by past events sorted by finish date in
     *         descending order.
     * @author Anton Bondar
     */
    @Query(nativeQuery = true, value = "SELECT e.* FROM events e "
        + "INNER JOIN events_dates_locations edl ON e.id = edl.event_id "
        + "WHERE edl.finish_date >= CURRENT_TIMESTAMP "
        + "UNION "
        + "SELECT e.* FROM events e "
        + "INNER JOIN events_dates_locations edl ON e.id = edl.event_id "
        + "WHERE edl.finish_date < CURRENT_TIMESTAMP "
        + "ORDER BY edl.finish_date DESC")
    Page<Event> findAllEventsSortedByStartDate(Pageable page);

    /**
     * Method for getting all events by user.
     *
     * @return list of {@link Event} instances.
     */
    @Query(
        value = "SELECT e FROM Event e LEFT JOIN e.attenders AS att WHERE att.id = :userId OR e.organizer.id =:userId")
    List<Event> findAllByAttenderOrOrganizer(Long userId);

    /**
     * Method for getting events created by User.
     *
     * @return list of {@link Event} instances.
     */
    @Query(value = "select e from Event e where e.organizer.id =:userId")
    Page<Event> findEventsByOrganizer(Pageable page, Long userId);

    /**
     * Method for getting pages of users events and events which were created by
     * this user.
     *
     * @return list of {@link Event} instances.
     */
    @Query(
        value = "select distinct e from Event e LEFT JOIN e.attenders AS att "
            + "WHERE e.organizer.id =:userId OR att.id = :userId ORDER BY e.id DESC")
    Page<Event> findRelatedEventsByUser(Pageable page, Long userId);

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

    /**
     * Get all events by event organizer.
     *
     * @param organizer {@link User}.
     */
    List<Event> getAllByOrganizer(User organizer);

    /**
     * Get all user's favorite events by user id.
     *
     * @param userId {@link Long}.
     */
    @Query(value = "SELECT e FROM Event e LEFT JOIN e.followers AS f WHERE f.id = :userId")
    Page<Event> findAllFavoritesByUser(Long userId, Pageable pageable);

    /**
     * Get subscribed events in given event ids by user id.
     */
    @Query(value = "SELECT e FROM Event e LEFT JOIN e.attenders AS f WHERE e.id in :eventIds AND f.id = :userId")
    List<Event> findSubscribedAmongEventIds(Collection<Long> eventIds, Long userId);

    /**
     * Get favorite events in given events by user id.
     */
    @Query(value = "SELECT e FROM Event e LEFT JOIN e.followers AS f WHERE e.id in :eventIds AND f.id = :userId")
    List<Event> findFavoritesAmongEventIds(Collection<Long> eventIds, Long userId);

    /**
     * Method returns {@link Event} by search queryand language code.
     *
     * @param pageable     {@link Pageable}
     * @param searchQuery  query to search
     * @param languageCode {@link String}
     *
     * @return Page of {@link Event} instances
     * @author Anton Bondar
     */
    @Query(nativeQuery = true, value = " SELECT distinct * FROM public.fn_searchevents "
        + "( :searchQuery, :languageCode) ")
    Page<Event> searchEvents(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Get all events addresses.
     *
     * @author Olena Sotnik.
     */
    @Query(value = "SELECT edl.address FROM Event e LEFT JOIN e.dates edl WHERE edl.address IS NOT NULL")
    Set<Address> findAllEventsAddresses();

    /**
     * Method returns count of all events where user is organizer or attender.
     *
     * @param userId {@link Long} id of current user.
     * @return {@link Long} count of organized or attended by user events.
     *
     * @author Olena Sotnik
     */
    @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT e.id) FROM events e "
        + "LEFT JOIN events_attenders att ON e.id = att.event_id "
        + "WHERE att.user_id = :userId OR e.organizer_id = :userId")
    Long getAmountOfOrganizedAndAttendedEventsByUserId(Long userId);

    /**
     * Method returns all events by their title.
     *
     * @param title {@link String} title of desired events.
     * @return List of {@link Event}.
     * @author Yurii Midianyi
     */
    List<Event> findAllByTitleContainingIgnoreCase(String title);
}

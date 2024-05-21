package greencity.repository;

import greencity.entity.User;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import jakarta.persistence.Tuple;
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

    /**
     * Retrieves a paginated list of Long in order based on various filter criteria
     * for unauthorized user.
     *
     * @param titleCriteria The criteria to match event titles against. This can be
     *                      a partial or full match.
     * @param isOpen        Indicates whether to filter events that are currently
     *                      open. If true, only open events are returned; if false,
     *                      closed events are returned; if null, this filter is
     *                      ignored.
     * @param isRelevant    Indicates whether to filter events that are relevant. If
     *                      true, only relevant events are returned; if false,
     *                      irrelevant events are returned; if null, this filter is
     *                      ignored.
     * @param citiesInLower An array of city names in lowercase to filter events by
     *                      location.
     * @param tagsInLower   An array of tags in lowercase to filter events by their
     *                      associated tags.
     * @param pageable      The pagination information.
     * @return A paginated list of EventPreviewDto objects that match the specified
     *         filters.
     */
    @Query(nativeQuery = true, value = """
        SELECT combined.eventId
        FROM (
                 (SELECT distinct e.id as eventId, e.title, min(edl.finish_date) as firstFinishDate,
                                  edl.city_en, tt.name as ttName, e.is_open,
                                  (true)             AS isRelevant,
                                  COUNT(DISTINCT eul) AS likes,
                                  COUNT(DISTINCT ec)  AS countComments,
                                  AVG(eg.grade)       AS grade
                  FROM events e
                           LEFT JOIN events_grades eg ON e.id = eg.event_id
                           LEFT JOIN events_comment ec ON e.id = ec.event_id
                           LEFT JOIN events_users_likes eul ON e.id = eul.event_id
                           LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                           LEFT JOIN events_tags et ON e.id = et.event_id
                           LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
                           LEFT JOIN languages l ON tt.language_id = l.id
                  WHERE (edl.finish_date >= now() and l.code='en')
                  GROUP BY e.id, tt.name, edl.city_en
                  ORDER BY firstFinishDate, grade DESC, likes DESC)
                 UNION ALL
                 (SELECT distinct e.id as eventId, e.title, min(edl.finish_date) as firstFinishDate,
                                  edl.city_en, tt.name as ttName, e.is_open,
                                  (false)             AS isRelevant,
                                  COUNT(DISTINCT eul) AS likes,
                                  COUNT(DISTINCT ec)  AS countComments,
                                  AVG(eg.grade)       AS grade
                  FROM events e
                           LEFT JOIN events_grades eg ON e.id = eg.event_id
                           LEFT JOIN events_comment ec ON e.id = ec.event_id
                           LEFT JOIN events_users_likes eul ON e.id = eul.event_id
                           LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                           LEFT JOIN events_tags et ON e.id = et.event_id
                           LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
                           LEFT JOIN languages l ON tt.language_id = l.id
                  WHERE (edl.finish_date < now() and l.code='en' and e.id not in
                                                                     (select e1.id from events e1
                                                                     left join events_dates_locations edl2
                                                                     on e1.id = edl2.event_id
                                                                     where edl2.finish_date >= now()))
                  GROUP BY e.id, tt.name, edl.city_en
                  ORDER BY firstFinishDate desc, grade DESC, likes DESC)) as combined
        WHERE (CAST(:titleCriteria as varchar) IS NULL OR lower(combined.title) like (:titleCriteria)) AND
            (CAST(:isOpen as boolean) IS NULL OR combined.is_open = :isOpen) AND
            (CAST(:isRelevant as boolean) IS NULL OR combined.isRelevant = :isRelevant) AND
            (CAST(:citiesInLower as varchar[]) IS NULL OR lower(combined.city_en) in (:citiesInLower)) AND
            (CAST(:tagsInLower as varchar[]) IS NULL OR lower(combined.ttName) in (:tagsInLower));
            """, countQuery = """
        SELECT count(distinct e.id)
         FROM events e
                  LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                  LEFT JOIN events_tags et ON e.id = et.event_id
                  LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
         WHERE (CAST(:titleCriteria as varchar) IS NULL OR lower(e.title) like (:titleCriteria)) AND
             (CAST(:isOpen as boolean) IS NULL OR e.is_open = :isOpen) AND
             (CAST(:isRelevant as boolean) IS NULL OR (edl.finish_date >= now()) = :isRelevant) AND
             (CAST(:citiesInLower as varchar[]) IS NULL OR lower(edl.city_en) in (:citiesInLower)) AND
             (CAST(:tagsInLower as varchar[]) IS NULL OR lower(tt.name) in (:tagsInLower))
            """)
    Page<Long> findAllEventPreviewDtoByFilters(String titleCriteria, Boolean isOpen,
        Boolean isRelevant, String[] citiesInLower, String[] tagsInLower, Pageable pageable);

    /**
     * Retrieves a paginated list of Long in order based on various filter criteria
     * for authorized user.
     *
     * @param userId            The ID of the user to filter events by.
     * @param isSubscribed      Indicates whether to filter events the user is
     *                          subscribed to. If true, only subscribed events are
     *                          returned; if false, unsubscribed events are
     *                          returned; if null, this filter is ignored.
     * @param isOrganizedByUser Indicates whether to filter events organized by the
     *                          user. If true, only events organized by the user are
     *                          returned; if false, events not organized by the user
     *                          are returned; if null, this filter is ignored.
     * @param isFavorite        Indicates whether to filter events marked as
     *                          favorite by the user. If true, only favorite events
     *                          are returned; if false, non-favorite events are
     *                          returned; if null, this filter is ignored.
     * @param titleCriteria     The criteria to match event titles against. This can
     *                          be a partial or full match.
     * @param isOpen            Indicates whether to filter events that are
     *                          currently open. If true, only open events are
     *                          returned; if false, closed events are returned; if
     *                          null, this filter is ignored.
     * @param isRelevant        Indicates whether to filter events that are
     *                          relevant. If true, only relevant events are
     *                          returned; if false, irrelevant events are returned;
     *                          if null, this filter is ignored.
     * @param citiesInLower     An array of city names in lowercase to filter events
     *                          by location.
     * @param tagsInLower       An array of tags in lowercase to filter events by
     *                          their associated tags.
     * @param pageable          The pagination information.
     * @return A paginated list of EventPreviewDto objects that match the specified
     *         filters.
     */
    @Query(nativeQuery = true,
        value = """
            SELECT combined.eventId
            FROM (
                     (SELECT distinct e.id as eventId, e.title, min(edl.finish_date) as firstFinishDate,
                             edl.city_en, tt.name as ttName, e.is_open,
                             (true)             AS isRelevant,
                             COUNT(DISTINCT eul) AS likes,
                             COUNT(DISTINCT ec)  AS countComments,
                             AVG(eg.grade)       AS grade,
                             (uf.friend_id IS NOT NULL)    AS isOrganizedByFriend,
                             (e.organizer_id = :userId) AS isOrganizedByUser,
                             (ea.user_id = :userId and ea.user_id is not null) AS isSubscribed,
                             (ef.user_id IS NOT NULL) AS isFavorite
                     FROM events e
                              LEFT JOIN events_grades eg ON e.id = eg.event_id
                              LEFT JOIN events_comment ec ON e.id = ec.event_id
                              LEFT JOIN events_users_likes eul ON e.id = eul.event_id
                              LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                              LEFT JOIN events_tags et ON e.id = et.event_id
                              LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
                              LEFT JOIN languages l ON tt.language_id = l.id
                              left join users_friends uf on uf.user_id = :userId and uf.friend_id=e.organizer_id
                                    and uf.status='FRIEND'
                              left join events_followers ef on e.id = ef.event_id and ef.user_id = :userId
                              left join events_attenders ea on e.id = ea.event_id and ea.user_id = :userId
                     WHERE (edl.finish_date >= now() and l.code='en')
                     GROUP BY e.id, uf.friend_id, tt.name, ea.user_id, edl.city_en, ef.user_id
                     ORDER BY isSubscribed desc, isFavorite desc, isOrganizedByUser desc, isOrganizedByFriend desc,
                        firstFinishDate, grade DESC, likes DESC)
                     UNION ALL
                     (SELECT distinct e.id as eventId, e.title, min(edl.finish_date) as firstFinishDate,
                                      edl.city_en, tt.name as ttName, e.is_open,
                                      (false)             AS isRelevant,
                                      COUNT(DISTINCT eul) AS likes,
                                      COUNT(DISTINCT ec)  AS countComments,
                                      AVG(eg.grade)       AS grade,
                                      (uf.friend_id IS NOT NULL)    AS isOrganizedByFriend,
                                      (e.organizer_id = :userId) AS isOrganizedByUser,
                                      (ea.user_id = :userId and ea.user_id is not null) AS isSubscribed,
                                      (ef.user_id IS NOT NULL) AS isFavorite
                      FROM events e
                               LEFT JOIN events_grades eg ON e.id = eg.event_id
                               LEFT JOIN events_comment ec ON e.id = ec.event_id
                               LEFT JOIN events_users_likes eul ON e.id = eul.event_id
                               LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                               LEFT JOIN events_tags et ON e.id = et.event_id
                               LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
                               LEFT JOIN languages l ON tt.language_id = l.id
                               left join users_friends uf on uf.user_id = :userId and uf.friend_id=e.organizer_id
                                    and uf.status='FRIEND'
                               left join events_followers ef on e.id = ef.event_id and ef.user_id = :userId
                               left join events_attenders ea on e.id = ea.event_id and ea.user_id = :userId
                     WHERE (edl.finish_date < now() and l.code='en' and e.id not in
                                                                        (select e1.id from events e1
                                                                        left join events_dates_locations edl2
                                                                        on e1.id = edl2.event_id
                                                                        where edl2.finish_date >= now()))
                      GROUP BY e.id, uf.friend_id, tt.name, ea.user_id, edl.city_en, ef.user_id
                      ORDER BY isSubscribed desc, isFavorite desc, isOrganizedByUser desc, isOrganizedByFriend desc,
                        firstFinishDate desc, grade DESC, likes DESC)
                 ) as combined
            WHERE (CAST(:titleCriteria as varchar) IS NULL OR lower(combined.title) like (:titleCriteria)) AND
                (CAST(:isOpen as boolean) IS NULL OR combined.is_open = :isOpen) AND
                (CAST(:isRelevant as boolean) IS NULL OR combined.isRelevant = :isRelevant) AND
                (CAST(:citiesInLower as varchar[]) IS NULL OR lower(combined.city_en) in (:citiesInLower)) AND
                (CAST(:tagsInLower as varchar[]) IS NULL OR lower(combined.ttName) in (:tagsInLower)) AND
                (CAST(:isSubscribed as boolean) IS NULL OR combined.isSubscribed = :isSubscribed) AND
                (CAST(:isOrganizedByUser as boolean) IS NULL OR combined.isOrganizedByUser = :isOrganizedByUser) AND
                (CAST(:isFavorite as boolean) IS NULL OR combined.isFavorite = :isFavorite);
                """,
        countQuery = """
            SELECT count(distinct e.id)
             FROM events e
                      LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                      LEFT JOIN events_tags et ON e.id = et.event_id
                      LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
                      left join events_followers ef on e.id = ef.event_id and ef.user_id = :userId
                      left join events_attenders ea on e.id = ea.event_id and ea.user_id = :userId
             WHERE (CAST(:titleCriteria as varchar) IS NULL OR lower(e.title) like (:titleCriteria)) AND
                 (CAST(:isOpen as boolean) IS NULL OR e.is_open = :isOpen) AND
                 (CAST(:isRelevant as boolean) IS NULL OR (edl.finish_date >= now()) = :isRelevant) AND
                 (CAST(:citiesInLower as varchar[]) IS NULL OR lower(edl.city_en) in (:citiesInLower)) AND
                 (CAST(:tagsInLower as varchar[]) IS NULL OR lower(tt.name) in (:tagsInLower)) AND
                 (CAST(:isSubscribed as boolean) IS NULL OR
                 (ea.user_id = :userId and ea.user_id is not null) = :isSubscribed) AND
                 (CAST(:isOrganizedByUser as boolean) IS NULL OR (e.organizer_id = :userId) = :isOrganizedByUser) AND
                 (CAST(:isFavorite as boolean) IS NULL OR (ef.user_id IS NOT NULL) = :isFavorite);
                """)
    Page<Long> findAllEventPreviewDtoByFilters(Long userId, Boolean isSubscribed, Boolean isOrganizedByUser,
        Boolean isFavorite, String titleCriteria, Boolean isOpen,
        Boolean isRelevant, String[] citiesInLower,
        String[] tagsInLower, Pageable pageable);

    /**
     * Retrieves a list of event preview data for the given event IDs for
     * unauthorized user. Does not support order from entry list.
     *
     * @param ids A list of event IDs for which the preview data is to be retrieved.
     * @return A list of Tuples containing the event preview data for the specified
     *         IDs.
     */
    @Query(nativeQuery = true, value = """
        SELECT e.id as eventId, e.title, et.tag_id as tagId, l.code as languageCode,
               tt.name as tagName, e.is_open, u.id as organizerId, u.name as organizerName,
               e.title_image, e.creation_date, start_date, finish_date, online_link,
               latitude, longitude, street_en, street_ua, house_number, city_en, city_ua,
               region_en, region_ua, country_en, country_ua, formatted_address_en,
               formatted_address_ua,
                        (true)             AS isRelevant,
                        COUNT(DISTINCT eul) AS likes,
                        COUNT(DISTINCT ec)  AS countComments,
                        AVG(eg.grade)       AS grade,
                        (false)    AS isOrganizedByFriend,
                        (false) AS isOrganizedByUser,
                        (false) AS isSubscribed,
                        (false) AS isFavorite
        FROM events e
                 LEFT JOIN events_grades eg ON e.id = eg.event_id
                 LEFT JOIN events_comment ec ON e.id = ec.event_id
                 LEFT JOIN events_users_likes eul ON e.id = eul.event_id
                 LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                 LEFT JOIN events_tags et ON e.id = et.event_id
                 LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
                 LEFT JOIN languages l ON tt.language_id = l.id
                 LEFT JOIN users u ON e.organizer_id = u.id
        WHERE (e.id in (:ids))
        GROUP BY e.id, tt.name, edl.city_en, et.tag_id, l.code, u.id, edl.id;
        """)
    List<Tuple> loadEventPreviewDataByIds(List<Long> ids);

    /**
     * Retrieves a list of event preview data for the given event IDs and user ID
     * for unauthorized user. Does not support order from entry list.
     *
     * @param ids    A list of event IDs for which the preview data is to be
     *               retrieved.
     * @param userId The ID of the user for which the event preview data is being
     *               retrieved.
     * @return A list of Tuples containing the event preview data for the specified
     *         IDs.
     */
    @Query(nativeQuery = true, value = """
        SELECT e.id as eventId, e.title, et.tag_id as tagId, l.code as languageCode,
               tt.name as tagName, e.is_open, u.id as organizerId, u.name as organizerName,
               e.title_image, e.creation_date, start_date, finish_date, online_link,
               latitude, longitude, street_en, street_ua, house_number, city_en, city_ua,
               region_en, region_ua, country_en, country_ua, formatted_address_en,
               formatted_address_ua,
               (true)             AS isRelevant,
               COUNT(DISTINCT eul) AS likes,
               COUNT(DISTINCT ec)  AS countComments,
               AVG(eg.grade)       AS grade,
               (uf.friend_id IS NOT NULL)    AS isOrganizedByFriend,
               (e.organizer_id = :userId) AS isOrganizedByUser,
               (ea.user_id = :userId and ea.user_id is not null) AS isSubscribed,
               (ef.user_id IS NOT NULL) AS isFavorite
        FROM events e
                 LEFT JOIN events_grades eg ON e.id = eg.event_id
                 LEFT JOIN events_comment ec ON e.id = ec.event_id
                 LEFT JOIN events_users_likes eul ON e.id = eul.event_id
                 LEFT JOIN events_dates_locations edl ON e.id = edl.event_id
                 LEFT JOIN events_tags et ON e.id = et.event_id
                 LEFT JOIN tag_translations tt ON et.tag_id = tt.tag_id
                 LEFT JOIN languages l ON tt.language_id = l.id
                 LEFT JOIN users u ON e.organizer_id = u.id
                 left join users_friends uf on uf.user_id = :userId and uf.friend_id=e.organizer_id
                    and uf.status='FRIEND'
                 left join events_followers ef on e.id = ef.event_id and ef.user_id = :userId
                 left join events_attenders ea on e.id = ea.event_id
        WHERE (e.id in (:ids))
        GROUP BY e.id, tt.name, edl.city_en, et.tag_id, l.code, u.id, edl.id, uf.friend_id, ea.user_id, ef.user_id;
        """)
    List<Tuple> loadEventPreviewDataByIds(List<Long> ids, Long userId);
}

package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventVO;
import greencity.dto.event.UpdateEventRequestDto;
import greencity.dto.filter.FilterEventDto;
import greencity.dto.search.SearchEventsDto;
import java.security.Principal;
import java.util.List;
import java.util.Set;
import greencity.dto.user.UserVO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    /**
     * Method for saving Event instance.
     *
     * @param addEventDtoRequest - dto.
     * @return {@link EventDto} instance.
     */
    EventDto save(AddEventDtoRequest addEventDtoRequest, String email, MultipartFile[] images);

    /**
     * Method for deleting Event instance.
     *
     * @param eventId - event id.
     * @param email   - user email.
     */
    void delete(Long eventId, String email);

    /**
     * Method for getting Event instance.
     *
     * @param eventId - event id.
     * @return {@link EventDto} instance.
     */
    EventDto getEvent(Long eventId, Principal principal);

    /**
     * Method for getting all Event instances filtered.
     *
     * @return List of {@link EventDto} instance.
     */
    PageableAdvancedDto<EventDto> getEvents(Pageable page, FilterEventDto filterEventDto, Long userId);

    /**
     * Method for getting all Event instances filtered.
     *
     * @return List of {@link EventDto} instance.
     */
    PageableAdvancedDto<EventDto> getEventsManagement(Pageable page, FilterEventDto filterEventDto, Long userId);

    /**
     * Add an attender to the Event by id.
     *
     * @param eventId - event id.
     */
    void addAttender(Long eventId, String email);

    /**
     * Remove an attender from the Event by id.
     *
     * @param eventId - event id.
     * @param email   - user email.
     */
    void removeAttender(Long eventId, String email);

    /**
     * Method for adding an event to favorites by event id.
     *
     * @param eventId - event id.
     * @param email   - user email.
     * @author Anton Bondar.
     */
    void addToFavorites(Long eventId, String email);

    /**
     * Method for removing an event from favorites by event id.
     *
     * @param eventId - event id.
     * @param email   - user email.
     * @author Anton Bondar.
     */
    void removeFromFavorites(Long eventId, String email);

    /**
     * Return Events searched by some query.
     *
     * @param paging - pagination params.
     * @param query  - query to search by.
     */
    PageableAdvancedDto<EventDto> searchEventsBy(Pageable paging, String query);

    /**
     * Update Event.
     *
     * @param email    - user that edits event
     * @param eventDto - new event information
     * @param images   - new images of event
     * @return EventDto
     */
    EventDto update(UpdateEventRequestDto eventDto, String email, MultipartFile[] images);

    /**
     * Rate Event.
     *
     * @param email   - user that rates event
     * @param eventId - id of rated event
     * @param grade   - grade of event
     */
    void rateEvent(Long eventId, String email, int grade);

    /**
     * Get all event attenders.
     *
     * @param eventId - id of event
     */
    Set<EventAttenderDto> getAllEventAttenders(Long eventId);

    /**
     * Get event by id.
     *
     * @param eventId - id of event
     */
    EventVO findById(Long eventId);

    /**
     * Method for getting Events by searchQuery.
     *
     * @param searchQuery  query to search
     * @param languageCode {@link String}
     * @return PageableDto of {@link SearchEventsDto} instances
     * @author Anton Bondar
     */
    PageableDto<SearchEventsDto> search(String searchQuery, String languageCode);

    /**
     * Method for getting Events by searchQuery.
     *
     * @param pageable     {@link Pageable}
     * @param searchQuery  query to search
     * @param languageCode {@link String}
     * @return PageableDto of {@link SearchEventsDto} instances
     * @author Anton Bondar
     */
    PageableDto<SearchEventsDto> search(Pageable pageable, String searchQuery, String languageCode);

    /**
     * Method for getting all events' addresses.
     *
     * @return list of {@link AddressDto} instances.
     * @author Olena Sotnik.
     */
    List<AddressDto> getAllEventsAddresses();

    /**
     * Method for getting amount of attended events by user id.
     *
     * @param userId {@link Long} user id.
     * @return {@link Long} amount of attended events by user id.
     */
    Long getCountOfAttendedEventsByUserId(Long userId);

    /**
     * Method for getting amount of organized events by user id.
     *
     * @param userId {@link Long} user id.
     * @return {@link Long} amount of organized events by user id.
     */
    Long getCountOfOrganizedEventsByUserId(Long userId);

    /**
     * Method to like or dislike {@link EventVO} specified by id.
     *
     * @param eventId id of {@link EventVO} to like/dislike.
     * @param userVO  current {@link UserVO} who wants to like/dislike.
     *
     * @author Roman Kasarab
     */
    void like(Long eventId, UserVO userVO);

    /**
     * Method to get amount of likes by event id.
     *
     * @param eventId - {@link Integer} event id.
     * @return amount of likes by event id.
     */
    int countLikes(Long eventId);

    /**
     * Method to check if user liked an event.
     *
     * @param eventId - id of {@link EventDto} to check liked or not.
     * @param userVO  - instance of {@link UserVO}.
     * @return user liked event or not.
     */
    boolean isEventLikedByUser(Long eventId, UserVO userVO);
}

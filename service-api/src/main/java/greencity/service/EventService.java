package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventVO;
import greencity.dto.event.UpdateEventDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Set;

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
     * Method for getting all Event instances.
     *
     * @return List of {@link EventDto} instance.
     */
    PageableAdvancedDto<EventDto> getAll(Pageable page, Principal principal);

    /**
     * Method for getting all Event instances that user attended.
     *
     * @return List of {@link EventDto} instance.
     */
    PageableAdvancedDto<EventDto> getAllUserEvents(Pageable page, String email);

    /**
     * Method for getting page of events which were created user.
     *
     * @return a page of{@link EventDto} instance.
     * @author Nikita Korzh.
     */
    PageableAdvancedDto<EventDto> getEventsCreatedByUser(Pageable pageable, String email);

    /**
     * Method for getting pages of users events and events which were created by
     * this user.
     *
     * @return a page of{@link EventDto} instance.
     */
    PageableAdvancedDto<EventDto> getRelatedToUserEvents(Pageable pageable, String name);

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
     * Method for saving an Event by ID.
     *
     * @param eventId - event id.
     * @param email   - user email.
     *
     * @author Anton Bondar.
     */
    void saveEvent(Long eventId, String email);

    /**
     * Method for undoing the saving of an Event by ID.
     *
     * @param eventId - event id.
     * @param email   - user email.
     *
     * @author Anton Bondar.
     */
    void undoSaveEvent(Long eventId, String email);

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
     *
     * @param eventDto - new event information
     * @param images   - new images of event
     * @return EventDto
     */
    EventDto update(UpdateEventDto eventDto, String email, MultipartFile[] images);

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
}

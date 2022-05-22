package greencity.service;

import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDto;
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
    EventDto getEvent(Long eventId);

    /**
     * Method for getting all Event instances.
     *
     * @return List of{@link EventDto} instance.
     */
    PageableAdvancedDto<EventDto> getAll(Pageable page);

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
}

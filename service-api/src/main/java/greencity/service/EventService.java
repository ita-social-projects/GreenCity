package greencity.service;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;

public interface EventService {
    /**
     * Method for saving Event instance.
     *
     * @param addEventDtoRequest - dto.
     * @return {@link AddEventDtoResponse} instance.
     */
    AddEventDtoResponse save(AddEventDtoRequest addEventDtoRequest, String email);

    /**
     * Add an attender to the  Event by id.
     *
     * @param eventId - event id.
     */
    void addAttender(Long eventId, String email);
}

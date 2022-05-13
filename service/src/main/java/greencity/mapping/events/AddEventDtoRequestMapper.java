package greencity.mapping.events;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventDateDto;
import greencity.entity.Coordinates;
import greencity.entity.Event;
import greencity.entity.EventDate;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that used by {@link ModelMapper} to map
 * {@link greencity.dto.event.AddEventDtoRequest} into
 * {@link greencity.entity.Event}.
 */
@Component
public class AddEventDtoRequestMapper extends AbstractConverter<AddEventDtoRequest, Event> {
    /**
     * Method for converting {@link greencity.dto.event.AddEventDtoRequest} into
     * {@link Event}.
     *
     * @param addEventDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    public Event convert(AddEventDtoRequest addEventDtoRequest) {
        Event event = new Event();

        event.setDescription(addEventDtoRequest.getDescription());
        if (addEventDtoRequest.getCoordinates() != null) {
            event.setCoordinates(Coordinates.builder()
                .latitude(addEventDtoRequest.getCoordinates().getLatitude())
                .longitude(addEventDtoRequest.getCoordinates().getLongitude())
                .build());
        } else {
            event.setOnlineLink(addEventDtoRequest.getOnlineLink());
        }
        event.setTitle(addEventDtoRequest.getTitle());
        List<EventDate> eventDates = new ArrayList<>();
        for (EventDateDto eventDateDto : addEventDtoRequest.getDates()) {
            eventDates.add(EventDate.builder().event(event).startDate(eventDateDto.getStartDate())
                .finishDate(eventDateDto.getFinishDate()).build());
        }
        event.setDates(eventDates);
        return event;
    }
}

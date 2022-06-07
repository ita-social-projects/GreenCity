package greencity.mapping.events;

import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.CoordinatesDto;
import greencity.entity.Coordinates;
import greencity.entity.Event;
import greencity.entity.EventDateLocation;
import greencity.exception.exceptions.BadRequestException;
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
@Component(value = "eventDtoRequestMapper")
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
        event.setTitle(addEventDtoRequest.getTitle());
        event.setDescription(addEventDtoRequest.getDescription());
        event.setOpen(addEventDtoRequest.isOpen());

        List<EventDateLocation> eventDateLocations = new ArrayList<>();
        for (var date : addEventDtoRequest.getDatesLocations()) {
            if (date.getCoordinates() == null && date.getOnlineLink() == null) {
                throw new BadRequestException("Coordinates or link should be set!");
            }
            EventDateLocation eventDateLocation = new EventDateLocation();
            eventDateLocation.setStartDate(date.getStartDate());
            eventDateLocation.setFinishDate(date.getFinishDate());
            if (date.getCoordinates() != null) {
                CoordinatesDto coordinatesDto = date.getCoordinates();
                eventDateLocation.setCoordinates(Coordinates.builder().latitude(coordinatesDto.getLatitude())
                    .longitude(coordinatesDto.getLongitude()).build());
            }
            if (date.getOnlineLink() != null) {
                eventDateLocation.setOnlineLink(date.getOnlineLink());
            }
            eventDateLocation.setEvent(event);
            eventDateLocations.add(eventDateLocation);
        }
        event.setDates(eventDateLocations);
        return event;
    }
}

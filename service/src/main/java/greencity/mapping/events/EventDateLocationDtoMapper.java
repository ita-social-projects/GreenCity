package greencity.mapping.events;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.entity.event.Coordinates;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link Event} into
 * {@link EventDto}.
 */
@Component
public class EventDateLocationDtoMapper extends AbstractConverter<EventDateLocationDto, EventDateLocation> {
    /**
     * Method for converting {@link EventDateLocationDto} into {@link EventDateLocation}.
     *
     * @param eventDateLocationDto object to convert.
     * @return converted object.
     */
    @Override
    public EventDateLocation convert(EventDateLocationDto eventDateLocationDto) {
        EventDateLocation eventDateLocation = new EventDateLocation();
        eventDateLocation.setStartDate(eventDateLocationDto.getStartDate());
        eventDateLocation.setFinishDate(eventDateLocationDto.getFinishDate());
        eventDateLocation.setId(eventDateLocationDto.getId());
        if (eventDateLocationDto.getOnlineLink() != null) {
            eventDateLocation.setOnlineLink(eventDateLocationDto.getOnlineLink());
        }
        if (eventDateLocationDto.getCoordinates() != null) {
            CoordinatesDto coordinatesDto = eventDateLocationDto.getCoordinates();
            eventDateLocation.setCoordinates(Coordinates.builder()
                    .latitude(coordinatesDto.getLatitude())
                    .longitude(coordinatesDto.getLongitude())
                    .addressUa(coordinatesDto.getAddressUa())
                    .addressEn(coordinatesDto.getAddressEn()).build());
        }
        return eventDateLocation;
    }
}

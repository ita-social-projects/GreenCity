package greencity.mapping;

import greencity.dto.event.AddEventDtoRequest;
import greencity.entity.Coordinates;
import greencity.entity.Event;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

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
    protected Event convert(AddEventDtoRequest addEventDtoRequest) {
        Event event = new Event();

        event.setDescription(addEventDtoRequest.getDescription());
        event.setCoordinates(Coordinates.builder()
            .latitude(addEventDtoRequest.getCoordinates().getLatitude())
            .longitude(addEventDtoRequest.getCoordinates().getLongitude())
            .build());
        event.setTitle(addEventDtoRequest.getTitle());
        event.setDateTime(addEventDtoRequest.getDateTime());

        return event;
    }
}

package greencity.mapping;

import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventAuthorDto;
import greencity.entity.Event;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link greencity.entity.Event}
 * into {@link AddEventDtoResponse}.
 */
@Component
public class AddEventDtoResponseMapper extends AbstractConverter<Event, AddEventDtoResponse> {
    /**
     * Method for converting {@link Event} into {@link AddEventDtoResponse}.
     *
     * @param event object to convert.
     * @return converted object.
     */
    @Override
    protected AddEventDtoResponse convert(Event event) {
        return AddEventDtoResponse.builder()
            .id(event.getId())
            .coordinates(CoordinatesDto.builder()
                .latitude(event.getCoordinates().getLatitude())
                .longitude(event.getCoordinates().getLongitude())
                .build())
            .dateTime(event.getDateTime())
            .description(event.getDescription())
            .organizer(EventAuthorDto.builder()
                .id(event.getOrganizer().getId())
                .name(event.getOrganizer().getName())
                .build())
            .build();
    }
}

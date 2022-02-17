package greencity.mapping;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDto;
import greencity.entity.Event;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link greencity.entity.Event}
 * into {@link EventDto}.
 */
@Component
public class EventDtoMapper extends AbstractConverter<Event, EventDto> {
    /**
     * Method for converting {@link Event} into {@link EventDto}.
     *
     * @param event object to convert.
     * @return converted object.
     */
    @Override
    protected EventDto convert(Event event) {
        return EventDto.builder()
            .id(event.getId())
            .coordinates(
                CoordinatesDto.builder()
                    .latitude(event.getCoordinates().getLatitude())
                    .longitude(event.getCoordinates().getLongitude())
                    .build())
            .dateTime(event.getDateTime())
            .description(event.getDescription())
            .images(event.getImages().stream()
                .map(image -> image.getLink()).collect(Collectors.toList()))
            .organizer(EventAuthorDto.builder()
                .name(event.getOrganizer().getName())
                .id(event.getOrganizer().getId())
                .build())
            .title(event.getTitle())
            .build();
    }
}

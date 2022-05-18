package greencity.mapping.events;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateDto;
import greencity.dto.event.EventDto;
import greencity.entity.Coordinates;
import greencity.entity.Event;
import greencity.entity.EventDateLocation;
import greencity.entity.EventImages;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
    public EventDto convert(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setDescription(event.getDescription());
        eventDto.setTitleImage(event.getTitleImage());
        eventDto.setOpen(event.isOpen());
        List<EventDateDto> datesLocations = new ArrayList<>();
        for (EventDateLocation eventDateLocation : event.getDates()) {
            EventDateDto eventDateDto = new EventDateDto();
            eventDateDto.setStartDate(eventDateLocation.getStartDate());
            eventDateDto.setFinishDate(eventDateLocation.getFinishDate());
            if (eventDateLocation.getOnlineLink() != null) {
                eventDateDto.setOnlineLink(eventDateLocation.getOnlineLink());
            }
            Coordinates coordinates = eventDateLocation.getCoordinates();
            if (coordinates != null) {
                CoordinatesDto coordinatesDto = CoordinatesDto.builder().latitude(coordinates.getLatitude())
                    .longitude(coordinates.getLongitude()).build();
                eventDateDto.setCoordinatesDto(coordinatesDto);
            }
            datesLocations.add(eventDateDto);
        }
        eventDto.setDates(datesLocations);
        eventDto.setOrganizer(EventAuthorDto.builder()
            .id(event.getOrganizer().getId())
            .name(event.getOrganizer().getName())
            .build());
        if (eventDto.getAdditionalImages() != null) {
            eventDto.setAdditionalImages(event.getAdditionalImages().stream()
                .map(EventImages::getLink).collect(Collectors.toList()));
        }
        return eventDto;
    }
}

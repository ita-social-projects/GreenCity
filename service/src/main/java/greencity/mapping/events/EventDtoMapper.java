package greencity.mapping.events;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateDto;
import greencity.dto.event.EventDto;
import greencity.entity.Event;
import greencity.entity.EventDate;
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
        List<EventDateDto> dates = new ArrayList<>();
        for (EventDate eventDate : event.getDates()) {
            dates.add(EventDateDto.builder().startDate(eventDate.getStartDate())
                .finishDate(eventDate.getFinishDate()).build());
        }
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setDates(dates);
        eventDto.setDescription(event.getDescription());
        eventDto.setOrganizer(EventAuthorDto.builder()
            .name(event.getOrganizer().getName())
            .id(event.getOrganizer().getId())
            .build());
        eventDto.setTitle(event.getTitle());
        eventDto.setTitleImage(event.getTitleImage());

        if (event.getCoordinates() != null) {
            eventDto.setCoordinates(CoordinatesDto.builder()
                .latitude(event.getCoordinates().getLatitude())
                .longitude(event.getCoordinates().getLongitude())
                .build());
        } else {
            eventDto.setOnlineLink(event.getOnlineLink());
        }
        eventDto.setAdditionalImages(event.getAdditionalImages().stream()
            .map(EventImages::getLink).collect(Collectors.toList()));

        return eventDto;
    }
}

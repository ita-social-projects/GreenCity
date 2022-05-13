package greencity.mapping.events;

import greencity.dto.event.EventDateDto;
import greencity.dto.event.EventDto;
import greencity.entity.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that used by {@link ModelMapper} to map {@link EventDto} into
 * {@link Event}.
 */
@Component
public class EventDtoToEventMapper extends AbstractConverter<EventDto, Event> {
    /**
     * Method for converting {@link EventDto} into {@link Event}.
     *
     * @param eventDto object to convert.
     * @return converted object.
     */

    @Override
    protected Event convert(EventDto eventDto) {
        List<EventDate> dates = new ArrayList<>();
        for (EventDateDto eventDate : eventDto.getDates()) {
            dates.add(EventDate.builder().startDate(eventDate.getStartDate())
                .finishDate(eventDate.getFinishDate()).build());
        }
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setDates(dates);
        event.setDescription(eventDto.getDescription());
        event.setOrganizer(User.builder()
            .name(eventDto.getOrganizer().getName())
            .id(eventDto.getOrganizer().getId())
            .build());
        event.setTitle(eventDto.getTitle());
        event.setTitleImage(eventDto.getTitleImage());

        if (event.getCoordinates() != null) {
            event.setCoordinates(Coordinates.builder()
                .latitude(event.getCoordinates().getLatitude())
                .longitude(event.getCoordinates().getLongitude())
                .build());
        } else {
            event.setOnlineLink(eventDto.getOnlineLink());
        }

        List<EventImages> eventImages = new ArrayList<>();
        for (String url : eventDto.getAdditionalImages()) {
            eventImages.add(EventImages.builder().link(url).event(event).build());
        }
        event.setAdditionalImages(eventImages);

        return event;
    }
}

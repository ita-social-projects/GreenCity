package greencity.mapping.events;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventDto;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImages;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that used by {@link ModelMapper} to map {@link EventDto} into
 * {@link Event}.
 */
@Component
@RequiredArgsConstructor
public class EventDtoToEventMapper extends AbstractConverter<EventDto, Event> {
    private final CoordinatesDtoMapper mapper;

    /**
     * Method for converting {@link EventDto} into {@link Event}.
     *
     * @param eventDto object to convert.
     * @return converted object.
     */
    @Override
    public Event convert(EventDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setTitle(eventDto.getTitle());
        event.setCreationDate(eventDto.getCreationDate());
        event.setDescription(eventDto.getDescription());
        event.setOrganizer(User.builder()
            .name(eventDto.getOrganizer().getName())
            .id(eventDto.getOrganizer().getId())
            .build());
        event.setTitleImage(eventDto.getTitleImage());

        if (eventDto.getAdditionalImages() != null) {
            List<EventImages> eventImages = new ArrayList<>();
            for (String url : eventDto.getAdditionalImages()) {
                eventImages.add(EventImages.builder().link(url).event(event).build());
            }
            event.setAdditionalImages(eventImages);
        }

        List<EventDateLocation> eventDateLocationsDto = new ArrayList<>();
        for (var date : eventDto.getDates()) {
            CoordinatesDto coordinatesDto = date.getCoordinates();
            eventDateLocationsDto.add(EventDateLocation.builder()
                .startDate(date.getStartDate())
                .finishDate(date.getFinishDate())
                .coordinates(mapper.convert(coordinatesDto))
                .onlineLink(date.getOnlineLink())
                .event(event).build());
        }
        event.setDates(eventDateLocationsDto);

        return event;
    }
}

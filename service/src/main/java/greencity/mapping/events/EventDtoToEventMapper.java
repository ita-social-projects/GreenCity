package greencity.mapping.events;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventDto;
import greencity.entity.*;
import greencity.entity.event.Coordinates;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImages;
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
    public Event convert(EventDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setTitle(eventDto.getTitle());
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
                .coordinates(Coordinates.builder().latitude(coordinatesDto.getLatitude())
                    .longitude(coordinatesDto.getLongitude())
                    .addressEn(coordinatesDto.getAddressEn())
                    .addressUa(coordinatesDto.getAddressUa()).build())
                .onlineLink(date.getOnlineLink())
                .event(event).build());
        }
        event.setDates(eventDateLocationsDto);

        return event;
    }
}

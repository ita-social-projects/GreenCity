package greencity.mapping.events;

import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateDto;
import greencity.entity.Event;
import greencity.entity.EventDate;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
    public AddEventDtoResponse convert(Event event) {
        List<EventDateDto> dates = new ArrayList<>();
        for (EventDate eventDate : event.getDates()) {
            dates.add(EventDateDto.builder().startDate(eventDate.getStartDate())
                .finishDate(eventDate.getFinishDate()).build());
        }
        AddEventDtoResponse addEventDtoResponse = new AddEventDtoResponse();
        addEventDtoResponse.setId(event.getId());
        addEventDtoResponse.setDates(dates);
        addEventDtoResponse.setDescription(event.getDescription());
        addEventDtoResponse.setOrganizer(EventAuthorDto.builder()
            .id(event.getOrganizer().getId())
            .name(event.getOrganizer().getName())
            .build());
        if (event.getCoordinates() != null) {
            addEventDtoResponse.setCoordinates(CoordinatesDto.builder()
                .latitude(event.getCoordinates().getLatitude())
                .longitude(event.getCoordinates().getLongitude())
                .build());
        } else {
            addEventDtoResponse.setOnlineLink(event.getOnlineLink());
        }
        return addEventDtoResponse;
    }
}

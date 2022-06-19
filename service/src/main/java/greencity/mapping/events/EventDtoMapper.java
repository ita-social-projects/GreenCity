package greencity.mapping.events;

import greencity.dto.event.CoordinatesDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagUaEnDto;
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
import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link Event}
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
        User organizer = event.getOrganizer();
        eventDto.setOrganizer(EventAuthorDto.builder().id(organizer.getId()).name(organizer.getName()).build());
        eventDto.setDates(event.getDates().stream().map(this::convertEventDateLocation).collect(Collectors.toList()));

        List<TagUaEnDto> tagUaEnDtos = new ArrayList<>();
        event.getTags().forEach(t -> {
            var translations = t.getTagTranslations();
            tagUaEnDtos.add(TagUaEnDto.builder().id(t.getId())
                .nameUa(translations.stream().filter(tr -> tr.getLanguage().getCode().equals("ua")).findFirst()
                    .orElseThrow().getName())
                .nameEn(translations.stream().filter(tr -> tr.getLanguage().getCode().equals("en")).findFirst()
                    .orElseThrow().getName())
                .build());
        });
        eventDto.setTags(tagUaEnDtos);

        if (event.getAdditionalImages() != null) {
            eventDto.setAdditionalImages(event.getAdditionalImages().stream()
                .map(EventImages::getLink).collect(Collectors.toList()));
        }
        return eventDto;
    }

    private EventDateLocationDto convertEventDateLocation(EventDateLocation eventDateLocation) {
        EventDateLocationDto eventDateLocationDto = new EventDateLocationDto();
        eventDateLocationDto.setStartDate(eventDateLocation.getStartDate());
        eventDateLocationDto.setFinishDate(eventDateLocation.getFinishDate());
        if (eventDateLocation.getOnlineLink() != null) {
            eventDateLocationDto.setOnlineLink(eventDateLocation.getOnlineLink());
        }
        Coordinates coordinates = eventDateLocation.getCoordinates();
        if (coordinates != null) {
            CoordinatesDto coordinatesDto = CoordinatesDto.builder().latitude(coordinates.getLatitude())
                .longitude(coordinates.getLongitude())
                .addressEn(coordinates.getAddressEn())
                .addressUa(coordinates.getAddressUa()).build();
            eventDateLocationDto.setCoordinates(coordinatesDto);
        }
        return eventDateLocationDto;
    }
}

package greencity.mapping.events;

import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.tag.TagUaEnDto;
import greencity.entity.User;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventGrade;
import greencity.entity.event.EventImages;
import greencity.enums.CommentStatus;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link Event} into
 * {@link EventDto}.
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
        eventDto.setCreationDate(event.getCreationDate());
        eventDto.setDescription(event.getDescription());
        eventDto.setTitleImage(event.getTitleImage());
        eventDto.setOpen(event.isOpen());
        eventDto.setIsRelevant(event.isRelevant());
        eventDto.setLikes(event.getUsersLikedEvents().size());
        eventDto
            .setCountComments((int) event.getEventsComments().stream()
                .filter(eventComment -> !eventComment.getStatus().equals(CommentStatus.DELETED)).count());
        User organizer = event.getOrganizer();
        eventDto.setOrganizer(
            EventAuthorDto.builder()
                .id(organizer.getId())
                .name(organizer.getName())
                .email(organizer.getEmail())
                .organizerRating(organizer.getEventOrganizerRating())
                .build());
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
        eventDto.setEventRate(calculateEventRate(event.getEventGrades()));
        return eventDto;
    }

    private EventDateLocationDto convertEventDateLocation(EventDateLocation eventDateLocation) {
        EventDateLocationDto eventDateLocationDto = new EventDateLocationDto();
        eventDateLocationDto.setStartDate(eventDateLocation.getStartDate());
        eventDateLocationDto.setFinishDate(eventDateLocation.getFinishDate());
        if (eventDateLocation.getOnlineLink() != null) {
            eventDateLocationDto.setOnlineLink(eventDateLocation.getOnlineLink());
        }
        Address address = eventDateLocation.getAddress();
        if (address != null) {
            AddressDto addressDto = AddressDto.builder().latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .streetEn(address.getStreetEn())
                .streetUa(address.getStreetUa())
                .houseNumber(address.getHouseNumber())
                .cityEn(address.getCityEn())
                .cityUa(address.getCityUa())
                .regionEn(address.getRegionEn())
                .regionUa(address.getRegionUa())
                .countryEn(address.getCountryEn())
                .countryUa(address.getCountryUa())
                .formattedAddressEn(address.getFormattedAddressEn())
                .formattedAddressUa(address.getFormattedAddressUa())
                .build();
            eventDateLocationDto.setCoordinates(addressDto);
        }
        return eventDateLocationDto;
    }

    private double calculateEventRate(List<EventGrade> eventGrades) {
        return eventGrades.stream()
            .mapToInt(EventGrade::getGrade)
            .average()
            .orElse(0.0);
    }
}
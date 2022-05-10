package greencity.service;

import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.*;
import greencity.entity.Event;
import greencity.entity.EventImages;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final FileService fileService;
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_HABIT_IMAGE;

    @Override
    public AddEventDtoResponse save(AddEventDtoRequest addEventDtoRequest, String email,
        MultipartFile[] images) {
        Event toSave = modelMapper.map(addEventDtoRequest, Event.class);
        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);
        toSave.setOrganizer(organizer);

        if (images != null && images.length > 0 && images[0] != null) {
            toSave.setTitleImage(fileService.upload(images[0]));
            List<EventImages> eventImages = new ArrayList<>();
            for (int i = 1; i < images.length; i++) {
                eventImages.add(EventImages.builder().event(toSave).link(fileService.upload(images[i])).build());
            }
            toSave.setImages(eventImages);
        } else {
            toSave.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }

        return modelMapper.map(eventRepo.save(toSave), AddEventDtoResponse.class);
    }

    @Override
    public void delete(Long eventId, String email) {
        User user = modelMapper.map(restClient.findByEmail(email), User.class);
        Event toDelete = eventRepo.getOne(eventId);
        if (toDelete.getOrganizer().getId().equals(user.getId())) {
            eventRepo.delete(toDelete);
        } else {
            throw new BadRequestException(ErrorMessage.NOT_EVENT_ORGANIZER);
        }
    }

    @Override
    public EventDto getEvent(Long eventId) {
        Event event = eventRepo.getOne(eventId);
        return getEventDto(event);
    }

    @Override
    public PageableAdvancedDto<EventDto> getAll(Pageable page) {
        Page<Event> pages = eventRepo.getAll(page);
        return buildPageableAdvancedDto(pages);
    }

    private PageableAdvancedDto<EventDto> buildPageableAdvancedDto(Page<Event> eventsPage) {
        List<EventDto> eventDtos = eventsPage.stream()
            .map(this::getEventDto)
            .collect(Collectors.toList());

        return new PageableAdvancedDto<>(
            eventDtos,
            eventsPage.getTotalElements(),
            eventsPage.getPageable().getPageNumber(),
            eventsPage.getTotalPages(),
            eventsPage.getNumber(),
            eventsPage.hasPrevious(),
            eventsPage.hasNext(),
            eventsPage.isFirst(),
            eventsPage.isLast());
    }

    @Override
    public void addAttender(Long eventId, String email) {
        Event event = eventRepo.getOne(eventId);
        User currentUser = modelMapper.map(restClient.findByEmail(email), User.class);

        if (event.getAttenders().stream().noneMatch(attender -> attender.getId().equals(currentUser.getId()))) {
            event.getAttenders().add(currentUser);
            eventRepo.save(event);
        } else {
            throw new BadRequestException(ErrorMessage.YOU_ARE_EVENT_ORGANIZER);
        }
    }

    @Override
    public void removeAttender(Long eventId, String email) {
        Event event = eventRepo.getOne(eventId);
        User currentUser = modelMapper.map(restClient.findByEmail(email), User.class);

        event.setAttenders(event.getAttenders().stream().filter(user -> !user.getId().equals(currentUser.getId()))
            .collect(Collectors.toSet()));

        eventRepo.save(event);
    }

    private EventDto getEventDto(Event event) {
        return EventDto.builder()
            .id(event.getId())
            .coordinates(modelMapper.map(event.getCoordinates(), CoordinatesDto.class))
            .description(event.getDescription())
            .organizer(modelMapper.map(event.getOrganizer(), EventAuthorDto.class))
            .title(event.getTitle())
            .titleImage(event.getTitleImage())
            .dateTime(event.getDateTime())
            .images(event.getImages() != null
                ? event.getImages().stream().map(EventImages::getLink).collect(Collectors.toList())
                : null)
            .build();
    }
}

package greencity.service;

import greencity.client.RestClient;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.EventDto;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final FileService fileService;

    @Override
    public AddEventDtoResponse save(AddEventDtoRequest addEventDtoRequest, String email) {
        Event toSave = modelMapper.map(addEventDtoRequest, Event.class);
        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);
        toSave.setOrganizer(organizer);

        MultipartFile multipartFile = null;
        if (addEventDtoRequest.getTitleImage() != null) {
            multipartFile = fileService.convertToMultipartImage(addEventDtoRequest.getTitleImage());
        }

        if (multipartFile != null) {
            toSave.setTitleImage(fileService.upload(multipartFile));
        }

        return modelMapper.map(eventRepo.save(toSave), AddEventDtoResponse.class);
    }

    @Override
    public void delete(Long eventId, String email) {
        User user = modelMapper.map(restClient.findByEmail(email), User.class);
        Event toDelete = eventRepo.getOne(eventId);
        if (toDelete.getOrganizer().getId() == user.getId()) {
            eventRepo.delete(toDelete);
        } else {
            throw new BadRequestException(ErrorMessage.NOT_EVENT_ORGANIZER);
        }
    }

    @Override
    public EventDto getEvent(Long eventId) {
        Event event = eventRepo.getOne(eventId);
        return modelMapper.map(event, EventDto.class);
    }

    @Override
    public PageableAdvancedDto<EventDto> getAll(Pageable page) {
        Page<Event> pages = eventRepo.getAll(page);
        return buildPageableAdvancedDto(pages);
    }

    private PageableAdvancedDto<EventDto> buildPageableAdvancedDto(Page<Event> eventsPage) {
        List<EventDto> eventDtos = eventsPage.stream()
            .map(event -> modelMapper.map(event, EventDto.class))
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

        if (event.getAttenders().stream().noneMatch(attender -> attender.getId() == currentUser.getId())) {
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

        event.setAttenders(event.getAttenders().stream().filter(user -> user.getId() != currentUser.getId())
            .collect(Collectors.toSet()));

        eventRepo.save(event);
    }
}

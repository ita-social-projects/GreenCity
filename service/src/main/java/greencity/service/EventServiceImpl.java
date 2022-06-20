package greencity.service;

import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.*;
import greencity.dto.tag.TagVO;
import greencity.entity.*;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImages;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private final TagsService tagService;
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_HABIT_IMAGE;

    @Override
    public EventDto save(AddEventDtoRequest addEventDtoRequest, String email,
        MultipartFile[] images) {
        Event toSave = modelMapper.map(addEventDtoRequest, Event.class);
        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);
        toSave.setOrganizer(organizer);
        if (images != null && images.length > 0 && images[0] != null) {
            toSave.setTitleImage(fileService.upload(images[0]));
            List<EventImages> eventImages = new ArrayList<>();
            for (int i = 1; i < images.length; i++) {
                if (images[i] != null) {
                    eventImages.add(EventImages.builder().event(toSave).link(fileService.upload(images[i])).build());
                }
            }
            toSave.setAdditionalImages(eventImages);
        } else {
            toSave.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }

        List<TagVO> tagVOs = tagService.findTagsWithAllTranslationsByNamesAndType(
            addEventDtoRequest.getTags(), TagType.EVENT);

        toSave.setTags(modelMapper.map(tagVOs,
            new TypeToken<List<Tag>>() {
            }.getType()));

        return modelMapper.map(eventRepo.save(toSave), EventDto.class);
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

    @Override
    public PageableAdvancedDto<EventDto> searchEventsBy(Pageable paging, String query) {
        Page<Event> page = eventRepo.searchEventsBy(paging, query);
        return buildPageableAdvancedDto(page);
    }

    /**
     * {@inheritDoc}
     *
     * @return EventDto
     */
    @Override
    public EventDto update(UpdateEventDto eventDto, String email, MultipartFile[] images) {
        Event toUpdate = modelMapper.map(getEvent(eventDto.getId()), Event.class);
        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);
        if (organizer.getRole() != Role.ROLE_ADMIN && !organizer.getId().equals(toUpdate.getOrganizer().getId())) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        enhanceWithNewData(toUpdate, eventDto, images);
        eventRepo.save(toUpdate);
        return modelMapper.map(toUpdate, EventDto.class);
    }

    private void enhanceWithNewData(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        if (updateEventDto.getTitle() != null) {
            toUpdate.setTitle(updateEventDto.getTitle());
        }
        if (updateEventDto.getDescription() != null) {
            toUpdate.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getIsOpen() != null) {
            toUpdate.setOpen(updateEventDto.getIsOpen());
        }

        if (updateEventDto.getTags() != null) {
            toUpdate.setTags(modelMapper.map(tagService
                .findTagsWithAllTranslationsByNamesAndType(updateEventDto.getTags(), TagType.EVENT),
                new TypeToken<List<Tag>>() {
                }.getType()));
        }

        List<String> additionalImagesStr = new ArrayList<>();
        if (updateEventDto.getAdditionalImages() != null) {
            additionalImagesStr.addAll(updateEventDto.getAdditionalImages());
        }

        if (updateEventDto.getImagesToDelete() != null) {
            for (String img : updateEventDto.getImagesToDelete()) {
                fileService.delete(img);
                toUpdate.getAdditionalImages()
                    .removeAll(toUpdate.getAdditionalImages().stream().filter(i -> i.getLink().equals(img))
                        .collect(Collectors.toList()));
            }
        }

        if (images.length > 0) {
            int imageCounter = 0;
            if (updateEventDto.getImagesToDelete().contains(toUpdate.getTitleImage())) {
                toUpdate.setTitleImage(fileService.upload(images[imageCounter++]));
            }
            for (int i = imageCounter; i < images.length; i++) {
                additionalImagesStr.add(fileService.upload(images[i]));
            }
        }
        toUpdate.setAdditionalImages(additionalImagesStr.stream().map(img -> EventImages.builder().event(toUpdate)
            .link(img).build()).collect(Collectors.toList()));

        if (updateEventDto.getDates() != null) {
            toUpdate.setDates(updateEventDto.getDates().stream().map(d -> modelMapper.map(d, EventDateLocation.class))
                .collect(Collectors.toList()));
        }
    }
}

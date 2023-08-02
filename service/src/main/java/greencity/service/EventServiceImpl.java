package greencity.service;

import com.google.maps.model.LatLng;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventVO;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.tag.TagVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventGrade;
import greencity.entity.event.EventImages;
import greencity.enums.EventType;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final RestClient restClient;
    private final FileService fileService;
    private final TagsService tagService;
    private final GoogleApiService googleApiService;
    private final UserService userService;
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_HABIT_IMAGE;
    private final UserRepo userRepo;

    @Override
    public EventDto save(AddEventDtoRequest addEventDtoRequest, String email,
                         MultipartFile[] images) {
        addAddressToLocation(addEventDtoRequest.getDatesLocations());
        Event toSave = modelMapper.map(addEventDtoRequest, Event.class);
        toSave.setCreationDate(LocalDate.now());
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
        List<String> eventImages = new ArrayList<>();
        eventImages.add(toDelete.getTitleImage());
        if (toDelete.getAdditionalImages() != null) {
            eventImages.addAll(toDelete.getAdditionalImages().stream().map(EventImages::getLink)
                    .collect(Collectors.toList()));
        }

        if (toDelete.getOrganizer().getId().equals(user.getId()) || user.getRole() == Role.ROLE_ADMIN) {
            deleteImagesFromServer(eventImages);
            eventRepo.delete(toDelete);
        } else {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
    }

    @Override
    public EventDto getEvent(Long eventId, Principal principal) {
        Event event =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        if (principal != null) {
            User currentUser = modelMapper.map(restClient.findByEmail(principal.getName()), User.class);
            eventDto.setIsSubscribed(event.getAttenders().stream()
                    .anyMatch(attender -> attender.getId().equals(currentUser.getId())));
        }
        return eventDto;
    }

    @Override
    public PageableAdvancedDto<EventDto> getAll(Pageable page, Principal principal) {
        Page<Event> events = eventRepo.findAllByOrderByIdDesc(page);
        PageableAdvancedDto<EventDto> eventDtos = buildPageableAdvancedDto(events);
        if (principal != null) {
            User user = modelMapper.map(restClient.findByEmail(principal.getName()), User.class);
            setSubscribes(events, eventDtos, user);
            setFollowers(events, eventDtos, user);
        }
        return eventDtos;
    }

    @Override
    public PageableAdvancedDto<EventDto> getAllUserEvents(
            Pageable page, String email, String userLatitude, String userLongitude, String eventType) {
        User attender = modelMapper.map(restClient.findByEmail(email), User.class);
        List<Event> events = sortUserEventsByEventType(eventType, attender, userLatitude, userLongitude);
        Page<Event> eventPage = new PageImpl<>(events, page, events.size());
        PageableAdvancedDto<EventDto> eventDtos = buildPageableAdvancedDto(eventPage);
        setSubscribes(eventPage, eventDtos, attender);
        return eventDtos;
    }

    private List<Event> sortUserEventsByEventType(
            String eventType, User attender, String userLatitude, String userLongitude) {
        if (eventType.equalsIgnoreCase("ONLINE")) {
            return getOnlineUserEventsSortedByDate(attender);
        }

        if (eventType.equalsIgnoreCase("OFFLINE")) {
            return (!userLatitude.isBlank() && !userLongitude.isBlank()) ?
                    getOfflineUserEventsSortedCloserToUserLocation(attender, userLatitude, userLongitude) :
                    getOfflineUserEventsSortedByDate(attender);
        }
        return eventRepo.findAllByAttender(attender.getId()).stream().sorted(getComparatorByDates())
                .collect(Collectors.toList());
    }

    private List<Event> getOnlineUserEventsSortedByDate(User attender) {
        return eventRepo.findAllByAttender(attender.getId()).stream()
                .filter(event -> event.getEventType().equals(EventType.ONLINE)
                        || event.getEventType().equals(EventType.ONLINE_OFFLINE))
                .sorted(getComparatorByDates())
                .collect(Collectors.toList());
    }

    private List<Event> getOfflineUserEventsSortedByDate(User attender) {
        return eventRepo.findAllByAttender(attender.getId()).stream()
                .filter(event -> event.getEventType().equals(EventType.OFFLINE)
                        || event.getEventType().equals(EventType.ONLINE_OFFLINE))
                .sorted(getComparatorByDates())
                .collect(Collectors.toList());
    }

    private List<Event> getOfflineUserEventsSortedCloserToUserLocation(
            User attender, String userLatitude, String userLongitude) {
        List<Event> eventsFurtherSorted = eventRepo.findAllByAttender(attender.getId()).stream()
                .filter(event -> event.getEventType().equals(EventType.OFFLINE)
                        || event.getEventType().equals(EventType.ONLINE_OFFLINE))
                .sorted(getComparatorByDistance(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude)))
                .filter(this::isEventRelevant)
                .collect(Collectors.toList());
        List<Event> eventsPassed = getOfflineUserEventsSortedByDate(attender).stream()
                .filter(event -> !isEventRelevant(event))
                .collect(Collectors.toList());
        eventsFurtherSorted.addAll(eventsPassed);
        return eventsFurtherSorted;
    }

    private boolean isEventRelevant(Event event) {
        return findLastEventDateTime(event).isAfter(ZonedDateTime.now())
                || findLastEventDateTime(event).isEqual(ZonedDateTime.now());
    }

    private Comparator<Event> getComparatorByDistance(final double userLatitude, final double userLongitude) {
        return (e1, e2) -> {
            double distance1 = calculateDistanceBetweenUserAndEventCoordinates(userLatitude, userLongitude,
                    Objects.requireNonNull(e1.getDates().get(e1.getDates().size() - 1)
                            .getAddress()).getLatitude(),
                    Objects.requireNonNull(e1.getDates().get(e1.getDates().size() - 1)
                            .getAddress()).getLongitude());
            double distance2 = calculateDistanceBetweenUserAndEventCoordinates(userLatitude, userLongitude,
                    Objects.requireNonNull(e2.getDates().get(e2.getDates().size() - 1)
                            .getAddress()).getLatitude(),
                    Objects.requireNonNull(e2.getDates().get(e2.getDates().size() - 1)
                            .getAddress()).getLongitude());
            return Double.compare(distance1, distance2);
        };
    }

    private Comparator<Event> getComparatorByDates() {
        return (e1, e2) -> e2.getDates().get(e2.getDates().size() - 1).getStartDate()
                .compareTo(e1.getDates().get(e1.getDates().size() - 1).getStartDate());
    }

    private double calculateDistanceBetweenUserAndEventCoordinates(
            double userLatitude, double userLongitude, double eventLatitude, double eventLongitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point userGeoPoint = geometryFactory.createPoint(new Coordinate(userLongitude, userLatitude));
        Point eventGeoPoint = geometryFactory.createPoint(new Coordinate(eventLongitude, eventLatitude));
        return userGeoPoint.distance(eventGeoPoint);
    }

    @Override
    public PageableAdvancedDto<EventDto> getEventsCreatedByUser(Pageable page, String email) {
        User attender = modelMapper.map(restClient.findByEmail(email), User.class);
        Page<Event> events = eventRepo.findEventsByOrganizer(page, attender.getId());
        PageableAdvancedDto<EventDto> eventDtos = buildPageableAdvancedDto(events);
        setSubscribes(events, eventDtos, attender);
        return eventDtos;
    }

    @Override
    public PageableAdvancedDto<EventDto> getRelatedToUserEvents(Pageable page, String email) {
        User attender = modelMapper.map(restClient.findByEmail(email), User.class);
        Page<Event> events = eventRepo.findRelatedEventsByUser(page, attender.getId());
        PageableAdvancedDto<EventDto> eventDtos = buildPageableAdvancedDto(events);
        setSubscribes(events, eventDtos, attender);
        return eventDtos;
    }

    private void setSubscribes(Page<Event> events, PageableAdvancedDto<EventDto> eventDtos, User user) {
        List<Long> eventIds = events.stream()
                .filter(event -> event.getAttenders().stream().map(User::getId).collect(Collectors.toList())
                        .contains(user.getId()))
                .map(Event::getId)
                .collect(Collectors.toList());
        eventDtos.getPage().forEach(eventDto -> eventDto.setIsSubscribed(eventIds.contains(eventDto.getId())));
    }

    private void setFollowers(Page<Event> events, PageableAdvancedDto<EventDto> eventDtos, User user) {
        List<Long> eventIds = events.stream()
                .filter(event -> event.getFollowers().stream().map(User::getId).collect(Collectors.toList())
                        .contains(user.getId()))
                .map(Event::getId)
                .collect(Collectors.toList());
        eventDtos.getPage().forEach(eventDto -> eventDto.setIsFavorite(eventIds.contains(eventDto.getId())));
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
        Event event =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        User currentUser = modelMapper.map(restClient.findByEmail(email), User.class);
        checkAttenderToJoinTheEvent(event, currentUser);
        event.getAttenders().add(currentUser);
        eventRepo.save(event);
    }

    private void checkAttenderToJoinTheEvent(Event event, User user) {
        if (Objects.equals(event.getOrganizer().getId(), user.getId())) {
            throw new BadRequestException(ErrorMessage.YOU_ARE_EVENT_ORGANIZER);
        } else if (!event.isOpen()
                && userRepo.findUserByIdAndByFriendId(user.getId(), event.getOrganizer().getId()).isEmpty()) {
            throw new BadRequestException(ErrorMessage.YOU_CANNOT_SUBSCRIBE_TO_CLOSE_EVENT);
        } else if (event.getAttenders().stream().anyMatch(a -> a.getId().equals(user.getId()))) {
            throw new BadRequestException(ErrorMessage.HAVE_ALREADY_SUBSCRIBED_ON_EVENT);
        }
    }

    @Override
    public void removeAttender(Long eventId, String email) {
        Event event =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        User currentUser = modelMapper.map(restClient.findByEmail(email), User.class);

        event.setAttenders(event.getAttenders().stream().filter(user -> !user.getId().equals(currentUser.getId()))
                .collect(Collectors.toSet()));

        eventRepo.save(event);
    }

    @Override
    public void addToFavorites(Long eventId, String email) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));

        User currentUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        if (event.getFollowers().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_ALREADY_ADDED_EVENT_TO_FAVORITES);
        }

        event.getFollowers().add(currentUser);
        eventRepo.save(event);
    }

    @Override
    public void removeFromFavorites(Long eventId, String email) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));

        User currentUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));

        if (!event.getFollowers().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_NOT_IN_FAVORITES);
        }

        event.setFollowers(event.getAttenders()
                .stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
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
    @Transactional
    public EventDto update(UpdateEventDto eventDto, String email, MultipartFile[] images) {
        Event toUpdate = eventRepo.findById(eventDto.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);

        if (organizer.getRole() != Role.ROLE_ADMIN && organizer.getRole() != Role.ROLE_MODERATOR
                && !organizer.getId().equals(toUpdate.getOrganizer().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        if (findLastEventDateTime(toUpdate).isBefore(ZonedDateTime.now())) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_FINISHED);
        }

        enhanceWithNewData(toUpdate, eventDto, images);
        return modelMapper.map(eventRepo.save(toUpdate), EventDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rateEvent(Long eventId, String email, int grade) {
        Event event =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        User currentUser = modelMapper.map(restClient.findByEmail(email), User.class);

        if (findLastEventDateTime(event).isAfter(ZonedDateTime.now())) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_NOT_FINISHED);
        }
        if (!event.getAttenders().stream().map(User::getId).collect(Collectors.toList())
                .contains(currentUser.getId())) {
            throw new BadRequestException(ErrorMessage.YOU_ARE_NOT_EVENT_SUBSCRIBER);
        }
        if (event.getEventGrades().stream().map(eventGrade -> eventGrade.getUser().getId()).collect(Collectors.toList())
                .contains(currentUser.getId())) {
            throw new BadRequestException(ErrorMessage.HAVE_ALREADY_RATED);
        }

        event.getEventGrades().add(EventGrade.builder().event(event).grade(grade).user(currentUser).build());
        eventRepo.save(event);

        userService.updateEventOrganizerRating(event.getOrganizer().getId(),
                calculateUserEventOrganizerRating(event.getOrganizer()));
    }

    @Override
    public Set<EventAttenderDto> getAllEventAttenders(Long eventId) {
        Event event =
                eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        return event.getAttenders().stream().map(attender -> modelMapper.map(attender, EventAttenderDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public EventVO findById(Long eventId) {
        Event event = eventRepo.findById(eventId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ECO_NEWS_NOT_FOUND_BY_ID + eventId));
        return modelMapper.map(event, EventVO.class);
    }

    private Double calculateUserEventOrganizerRating(User user) {
        List<Event> events = eventRepo.getAllByOrganizer(user);
        int summaryGrade = 0;
        int reviewsAmount = 0;
        for (var event : events) {
            for (var grade : event.getEventGrades()) {
                summaryGrade += grade.getGrade();
                reviewsAmount++;
            }
        }
        double finalRating = 0;
        if (reviewsAmount != 0) {
            finalRating = ((double) summaryGrade) / reviewsAmount;
        }
        return finalRating;
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

        updateImages(toUpdate, updateEventDto, images);

        if (updateEventDto.getDatesLocations() != null) {
            addAddressToLocation(updateEventDto.getDatesLocations());
            eventRepo.deleteEventDateLocationsByEventId(toUpdate.getId());
            toUpdate.setDates(updateEventDto.getDatesLocations().stream()
                    .map(d -> modelMapper.map(d, EventDateLocation.class))
                    .map(d -> {
                        d.setEvent(toUpdate);
                        return d;
                    })
                    .collect(Collectors.toList()));
        }
    }

    private void updateImages(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        eventRepo.deleteEventAdditionalImagesByEventId(updateEventDto.getId());
        if (ArrayUtils.isEmpty(images) && updateEventDto.getImagesToDelete() == null) {
            changeOldImagesWithoutRemovingAndAdding(toUpdate, updateEventDto);
        } else if (images == null || images.length == 0) {
            deleteOldImages(toUpdate, updateEventDto);
        } else if (updateEventDto.getImagesToDelete() == null) {
            addNewImages(toUpdate, updateEventDto, images);
        } else {
            deleteImagesFromServer(updateEventDto.getImagesToDelete());
            addNewImages(toUpdate, updateEventDto, images);
        }
    }

    private void changeOldImagesWithoutRemovingAndAdding(Event toUpdate, UpdateEventDto updateEventDto) {
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
        } else {
            toUpdate.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }
        if (updateEventDto.getAdditionalImages() != null) {
            updateEventDto.getAdditionalImages().forEach(img -> toUpdate
                    .setAdditionalImages(List.of(EventImages.builder().link(img).event(toUpdate).build())));
        } else {
            toUpdate.setAdditionalImages(null);
        }
    }

    private void deleteOldImages(Event toUpdate, UpdateEventDto updateEventDto) {
        deleteImagesFromServer(updateEventDto.getImagesToDelete());
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
            if (updateEventDto.getAdditionalImages() != null) {
                toUpdate.setAdditionalImages(updateEventDto.getAdditionalImages().stream()
                        .map(url -> EventImages.builder().event(toUpdate).link(url).build())
                        .collect(Collectors.toList()));
            } else {
                toUpdate.setAdditionalImages(null);
            }
        } else {
            toUpdate.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }
    }

    private void deleteImagesFromServer(List<String> images) {
        images.stream().filter(img -> !img.equals(DEFAULT_TITLE_IMAGE_PATH)).forEach(fileService::delete);
    }

    private void addNewImages(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        int imagesCounter = 0;
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
        } else {
            toUpdate.setTitleImage(fileService.upload(images[imagesCounter++]));
        }
        List<String> additionalImagesStr = new ArrayList<>();
        if (updateEventDto.getAdditionalImages() != null) {
            additionalImagesStr.addAll(updateEventDto.getAdditionalImages());
        }
        for (int i = imagesCounter; i < images.length; i++) {
            additionalImagesStr.add(fileService.upload(images[imagesCounter++]));
        }
        if (!additionalImagesStr.isEmpty()) {
            toUpdate.setAdditionalImages(additionalImagesStr.stream().map(url -> EventImages.builder()
                    .event(toUpdate).link(url).build()).collect(Collectors.toList()));
        } else {
            toUpdate.setAdditionalImages(null);
        }
    }

    private void addAddressToLocation(List<EventDateLocationDto> eventDateLocationDtos) {
        eventDateLocationDtos
                .stream()
                .filter(eventDateLocationDto -> Objects.nonNull(eventDateLocationDto.getCoordinates()))
                .forEach(eventDateLocationDto -> {
                    AddressDto addressDto = eventDateLocationDto.getCoordinates();
                    AddressLatLngResponse response = googleApiService.getResultFromGeoCodeByCoordinates(
                            new LatLng(addressDto.getLatitude(), addressDto.getLongitude()));
                    eventDateLocationDto.setCoordinates(modelMapper.map(response, AddressDto.class));
                });
    }

    private ZonedDateTime findLastEventDateTime(Event event) {
        return Collections
                .max(event.getDates().stream().map(EventDateLocation::getFinishDate).collect(Collectors.toList()));
    }
}

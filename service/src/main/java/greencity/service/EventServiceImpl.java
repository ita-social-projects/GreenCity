package greencity.service;

import com.google.maps.model.LatLng;
import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.constant.EmailNotificationMessagesConstants;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventVO;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.filter.FilterEventDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventGrade;
import greencity.entity.event.EventImages;
import greencity.entity.event.Address;
import greencity.enums.EventType;
import greencity.enums.TagType;
import greencity.enums.Role;
import greencity.enums.AchievementCategoryType;
import greencity.enums.AchievementAction;
import greencity.enums.RatingCalculationEnum;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.message.GeneralEmailMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.EventRepo;
import greencity.repository.EventsSearchRepo;
import greencity.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static greencity.constant.ErrorMessage.PAGE_NOT_FOUND;

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
    private final EventsSearchRepo eventsSearchRepo;
    private final NotificationService notificationService;
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_EVENT_IMAGES;
    private final UserRepo userRepo;
    private final RatingCalculation ratingCalculation;
    private final AchievementCalculation achievementCalculation;

    private static final String FUTURE_EVENT = "FUTURE";
    private static final String PAST_EVENT = "PAST";
    private static final String ONLINE_EVENT = "ONLINE";
    private static final String OFFLINE_EVENT = "OFFLINE";
    private static final String OPEN_STATUS = "OPEN";
    private static final String CLOSED_STATUS = "CLOSED";
    private static final String JOINED_STATUS = "JOINED";
    private static final String CREATED_STATUS = "CREATED";
    private static final String SAVED_STATUS = "SAVED";
    private static final String ECONOMIC_TAG = "ECONOMIC";
    private static final String ENVIRONMENTAL_TAG = "ENVIRONMENTAL";
    private static final String SOCIAL_TAG = "SOCIAL";

    @Override
    public EventDto save(AddEventDtoRequest addEventDtoRequest, String email,
        MultipartFile[] images) {
        addAddressToLocation(addEventDtoRequest.getDatesLocations());
        Event toSave = modelMapper.map(addEventDtoRequest, Event.class);
        toSave.setCreationDate(LocalDate.now());
        UserVO userVO = restClient.findByEmail(email);
        User organizer = modelMapper.map(userVO, User.class);
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

        Event savedEvent = eventRepo.save(toSave);
        achievementCalculation.calculateAchievement(userVO, AchievementCategoryType.CREATE_EVENT,
            AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.CREATE_EVENT, userVO);
        notificationService.sendEmailNotification(GeneralEmailMessage.builder()
            .email(organizer.getEmail())
            .subject(EmailNotificationMessagesConstants.EVENT_CREATION_SUBJECT)
            .message(String.format(EmailNotificationMessagesConstants.EVENT_CREATION_MESSAGE,
                savedEvent.getTitle()))
            .build());
        return buildEventDto(savedEvent, organizer.getId());
    }

    @Override
    public void delete(Long eventId, String email) {
        UserVO userVO = restClient.findByEmail(email);
        Event toDelete =
            eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        List<String> eventImages = new ArrayList<>();
        eventImages.add(toDelete.getTitleImage());
        if (toDelete.getAdditionalImages() != null) {
            eventImages.addAll(toDelete.getAdditionalImages().stream().map(EventImages::getLink)
                .collect(Collectors.toList()));
        }

        if (toDelete.getOrganizer().getId().equals(userVO.getId()) || userVO.getRole() == Role.ROLE_ADMIN) {
            deleteImagesFromServer(eventImages);
            Set<String> attendersEmails =
                toDelete.getAttenders().stream().map(User::getEmail).collect(Collectors.toSet());
            notificationService.sendEmailNotification(
                attendersEmails,
                EmailNotificationMessagesConstants.EVENT_CANCELED_SUBJECT,
                String.format(EmailNotificationMessagesConstants.EVENT_CANCELED_MESSAGE, toDelete.getTitle()));
            eventRepo.delete(toDelete);
        } else {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.CREATE_EVENT, AchievementAction.DELETE);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_CREATE_EVENT, userVO);
    }

    @Override
    public EventDto getEvent(Long eventId, Principal principal) {
        Event event =
            eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        if (principal != null) {
            User currentUser = modelMapper.map(restClient.findByEmail(principal.getName()), User.class);
            return buildEventDto(event, currentUser.getId());
        }
        return buildEventDto(event);
    }

    @Override
    public PageableAdvancedDto<EventDto> getAll(Pageable page, Principal principal) {
        Page<Event> events = eventRepo.findAllByOrderByIdDesc(page);

        if (principal != null) {
            User user = modelMapper.map(restClient.findByEmail(principal.getName()), User.class);
            return buildPageableAdvancedDto(events, user.getId());
        }
        return buildPageableAdvancedDto(events);
    }

    @Override
    public PageableAdvancedDto<EventDto> getEvents(Pageable page, Principal principal, FilterEventDto filterEventDto,
        String title) {
        if (Objects.isNull(filterEventDto)) {
            return getAll(page, principal);
        }
        return getAllFiltered(principal, page, filterEventDto, title);
    }

    private PageableAdvancedDto<EventDto> getAllFiltered(Principal principal, Pageable page,
        FilterEventDto filterEventDto, String title) {
        List<Event> events = title == null ? eventRepo.findAll() : eventRepo.findAllByTitleContainingIgnoreCase(title);

        validatePageNumber(events, page);

        return principal != null
            ? getFilteredForLoggedInUser(events, page, principal, filterEventDto)
            : getFilteredForAnonymousUser(events, page, filterEventDto);
    }

    private void validatePageNumber(List<Event> events, Pageable page) {
        int pageNumber = page.getPageNumber();
        int totalPages = (int) Math.ceil((double) events.size() / (double) page.getPageSize());

        if (pageNumber > totalPages) {
            throw new NotFoundException(PAGE_NOT_FOUND + totalPages);
        }
    }

    private PageableAdvancedDto<EventDto> getFilteredForLoggedInUser(List<Event> events, Pageable page,
        Principal principal, FilterEventDto filterEventDto) {
        long userId = modelMapper.map(restClient.findByEmail(principal.getName()), User.class).getId();
        events = getAllFilteredAndSorted(events, userId, filterEventDto);
        validatePageNumber(events, page);
        Page<Event> eventPage = new PageImpl<>(getEventsForCurrentPage(page, events), page, events.size());
        return buildPageableAdvancedDto(eventPage, userId);
    }

    private PageableAdvancedDto<EventDto> getFilteredForAnonymousUser(List<Event> events, Pageable page,
        FilterEventDto filterEventDto) {
        events = getAllFilteredAndSorted(events, null, filterEventDto);
        validatePageNumber(events, page);
        Page<Event> eventPage = new PageImpl<>(getEventsForCurrentPage(page, events), page, events.size());
        return buildPageableAdvancedDto(eventPage);
    }

    @Override
    public PageableAdvancedDto<EventDto> getAllUserEvents(
        Pageable page, String email, String userLatitude, String userLongitude, String eventType) {
        User attender = modelMapper.map(restClient.findByEmail(email), User.class);
        List<Event> events = sortUserEventsByEventType(eventType, attender, userLatitude, userLongitude);
        Page<Event> eventPage = new PageImpl<>(getEventsForCurrentPage(page, events), page, events.size());
        return buildPageableAdvancedDto(eventPage, attender.getId());
    }

    @Override
    public PageableAdvancedDto<EventDto> getAllFavoriteEventsByUser(Pageable page, Principal principal,
        FilterEventDto filterEventDto, String title) {
        User user = modelMapper.map(restClient.findByEmail(principal.getName()), User.class);
        List<Event> events = title == null
            ? eventRepo.findAllFavoritesByUser(user.getId())
            : eventRepo.findAllFavoritesByUserAndTitle(user.getId(), title);
        validatePageNumber(events, page);
        return getFilteredForLoggedInUser(events, page, principal, filterEventDto);
    }

    @Override
    public Set<AddressDto> getAllEventsAddresses() {
        return eventRepo.findAllEventsAddresses().stream()
            .map(eventAddress -> modelMapper.map(eventAddress, AddressDto.class))
            .collect(Collectors.toSet());
    }

    private List<Event> getEventsForCurrentPage(Pageable page, List<Event> allEvents) {
        int startIndex = page.getPageNumber() * page.getPageSize();
        int endIndex = Math.min(startIndex + page.getPageSize(), allEvents.size());
        return allEvents.subList(startIndex, endIndex);
    }

    private List<Event> sortUserEventsByEventType(
        String eventType, User attender, String userLatitude, String userLongitude) {
        if (StringUtils.isNotBlank(eventType)) {
            if (ONLINE_EVENT.equalsIgnoreCase(eventType)) {
                return getOnlineUserEventsSortedByDate(attender);
            }
            if (OFFLINE_EVENT.equalsIgnoreCase(eventType)) {
                return (StringUtils.isNotBlank(userLatitude) && StringUtils.isNotBlank(userLongitude))
                    ? getOfflineUserEventsSortedByUserLocation(attender, userLatitude, userLongitude)
                    : getOfflineUserEventsSortedByDate(attender);
            } else {
                throw new BadRequestException(ErrorMessage.INVALID_EVENT_TYPE);
            }
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

    private List<Event> getOfflineUserEventsSortedByUserLocation(
        User attender, String userLatitude, String userLongitude) {
        List<Event> eventsFurtherSorted = eventRepo.findAllByAttender(attender.getId()).stream()
            .filter(event -> event.getEventType().equals(EventType.OFFLINE)
                || event.getEventType().equals(EventType.ONLINE_OFFLINE))
            .sorted(getComparatorByDistance(Double.parseDouble(userLatitude), Double.parseDouble(userLongitude)))
            .filter(Event::isRelevant)
            .collect(Collectors.toList());
        List<Event> eventsPassed = getOfflineUserEventsSortedByDate(attender).stream()
            .filter(event -> !event.isRelevant())
            .collect(Collectors.toList());
        eventsFurtherSorted.addAll(eventsPassed);
        return eventsFurtherSorted;
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
        return buildPageableAdvancedDto(events, attender.getId());
    }

    @Override
    public PageableAdvancedDto<EventDto> getRelatedToUserEvents(Pageable page, String email) {
        User attender = modelMapper.map(restClient.findByEmail(email), User.class);
        Page<Event> events = eventRepo.findRelatedEventsByUser(page, attender.getId());
        return buildPageableAdvancedDto(events, attender.getId());
    }

    @Override
    public void addAttender(Long eventId, String email) {
        Event event =
            eventRepo.findById(eventId).orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        UserVO userVO = restClient.findByEmail(email);
        User currentUser = modelMapper.map(userVO, User.class);
        checkAttenderToJoinTheEvent(event, currentUser);
        event.getAttenders().add(currentUser);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.JOIN_EVENT, AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.JOIN_EVENT, userVO);
        eventRepo.save(event);
        notificationService.sendEmailNotification(GeneralEmailMessage.builder()
            .email(event.getOrganizer().getEmail())
            .subject(EmailNotificationMessagesConstants.EVENT_JOINED_SUBJECT)
            .message(String.format(EmailNotificationMessagesConstants.EVENT_JOINED_MESSAGE, currentUser.getName()))
            .build());
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
        UserVO userVO = restClient.findByEmail(email);
        event.setAttenders(event.getAttenders().stream().filter(user -> !user.getId().equals(userVO.getId()))
            .collect(Collectors.toSet()));
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.JOIN_EVENT, AchievementAction.DELETE);
        ratingCalculation.ratingCalculation(RatingCalculationEnum.UNDO_JOIN_EVENT, userVO);
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
        Event updatedEvent = eventRepo.save(toUpdate);
        Set<String> emailsToNotify = toUpdate.getAttenders().stream().map(User::getEmail).collect(Collectors.toSet());
        emailsToNotify.add(organizer.getEmail());
        notificationService.sendEmailNotification(
            emailsToNotify,
            EmailNotificationMessagesConstants.EVENT_UPDATED_SUBJECT,
            String.format(EmailNotificationMessagesConstants.EVENT_UPDATED_MESSAGE, toUpdate.getTitle()));
        return buildEventDto(updatedEvent, organizer.getId());
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
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
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

    private List<Event> getAllFilteredAndSorted(
        List<Event> allEvents, Long userId, FilterEventDto filterEventDto) {
        List<Event> filtered = getFilteredByEventTimeAndCitiesAndTags(allEvents, filterEventDto);

        if (CollectionUtils.isNotEmpty(filterEventDto.getStatuses())) {
            if (userId != null) {
                filtered = filterByAllStatuses(filtered, filterEventDto.getStatuses(), userId);
            } else {
                filtered = filterByStatusOpenClosed(filtered, filterEventDto.getStatuses());
            }
        }
        return getSortedListByEventId(filtered);
    }

    private List<Event> getFilteredByEventTimeAndCitiesAndTags(List<Event> allEvents, FilterEventDto filterEventDto) {
        List<Event> filtered = getEventsByEventTimeCondition(allEvents, filterEventDto.getEventTime());

        if (CollectionUtils.isNotEmpty(filterEventDto.getCities())) {
            filtered = filterByLocation(filtered, filterEventDto.getCities());
        }
        if (CollectionUtils.isNotEmpty(filterEventDto.getTags())) {
            filtered = filterByTags(filtered, filterEventDto.getTags());
        }
        return filtered;
    }

    private List<Event> getEventsByEventTimeCondition(List<Event> allEvents, List<String> eventTimes) {
        return (CollectionUtils.isNotEmpty(eventTimes))
            ? filterByTime(allEvents, eventTimes)
            : allEvents;
    }

    private List<Event> filterByTime(List<Event> events, List<String> eventTimes) {
        List<Event> filteredByTime = new ArrayList<>();
        for (String time : eventTimes) {
            if (FUTURE_EVENT.equalsIgnoreCase(time)) {
                filteredByTime.addAll(getFutureEvents(events));
            }
            if (PAST_EVENT.equalsIgnoreCase(time)) {
                filteredByTime.addAll(getPastEvents(events));
            }
        }
        return filteredByTime;
    }

    private List<Event> filterByLocation(List<Event> events, List<String> locations) {
        return events.stream()
            .filter(event -> event.getDates().stream()
                .map(EventDateLocation::getAddress)
                .filter(Objects::nonNull)
                .map(Address::getCityEn)
                .anyMatch(locations::contains))
            .collect(Collectors.toList());
    }

    private List<Event> filterByAllStatuses(List<Event> events, List<String> statuses, Long userId) {
        List<Event> filteredByStatus = new ArrayList<>();
        for (String status : statuses) {
            if (OPEN_STATUS.equalsIgnoreCase(status)) {
                filteredByStatus.addAll(getOpenEvents(events));
            }
            if (CLOSED_STATUS.equalsIgnoreCase(status)) {
                filteredByStatus.addAll(getClosedEvents(events));
            }
            if (JOINED_STATUS.equalsIgnoreCase(status)) {
                filteredByStatus.addAll(getJoinedEvents(events, userId));
            }
            if (CREATED_STATUS.equalsIgnoreCase(status)) {
                filteredByStatus.addAll(getCreatedEvents(events, userId));
            }
            if (SAVED_STATUS.equalsIgnoreCase(status)) {
                filteredByStatus.addAll(getSavedEvents(events, userId));
            }
        }
        return filteredByStatus;
    }

    private List<Event> filterByStatusOpenClosed(List<Event> events, List<String> statuses) {
        List<Event> filteredByStatus = new ArrayList<>();
        for (String status : statuses) {
            if (OPEN_STATUS.equalsIgnoreCase(status)) {
                filteredByStatus.addAll(getOpenEvents(events));
            }
            if (CLOSED_STATUS.equalsIgnoreCase(status)) {
                filteredByStatus.addAll(getClosedEvents(events));
            }
        }
        return filteredByStatus;
    }

    private List<Event> filterByTags(List<Event> events, List<String> tags) {
        List<Event> filteredByTags = new ArrayList<>();
        tags.stream().filter(Objects::nonNull).map(String::toUpperCase).forEach(tag -> {
            switch (tag) {
                case ECONOMIC_TAG:
                    filteredByTags.addAll(getEventsByTagName(events, ECONOMIC_TAG));
                    break;
                case ENVIRONMENTAL_TAG:
                    filteredByTags.addAll(getEventsByTagName(events, ENVIRONMENTAL_TAG));
                    break;
                case SOCIAL_TAG:
                    filteredByTags.addAll(getEventsByTagName(events, SOCIAL_TAG));
                    break;
                default:
                    break;
            }
        });
        return filteredByTags;
    }

    private List<Event> getFutureEvents(List<Event> events) {
        return events.stream().filter(Event::isRelevant).collect(Collectors.toList());
    }

    private List<Event> getPastEvents(List<Event> events) {
        return events.stream().filter(event -> !event.isRelevant()).collect(Collectors.toList());
    }

    private List<Event> getOpenEvents(List<Event> events) {
        return events.stream().filter(Event::isOpen).collect(Collectors.toList());
    }

    private List<Event> getClosedEvents(List<Event> events) {
        return events.stream().filter(event -> !event.isOpen()).collect(Collectors.toList());
    }

    private List<Event> getJoinedEvents(List<Event> events, Long userId) {
        return events.stream().filter(event -> event.getAttenders().stream().map(User::getId)
            .collect(Collectors.toList()).contains(userId)).collect(Collectors.toList());
    }

    private List<Event> getCreatedEvents(List<Event> events, Long userId) {
        return events.stream().filter(event -> event.getOrganizer().getId().equals(userId))
            .collect(Collectors.toList());
    }

    private List<Event> getSavedEvents(List<Event> events, Long userId) {
        return events.stream().filter(event -> event.getFollowers().stream().map(User::getId)
            .collect(Collectors.toList()).contains(userId)).collect(Collectors.toList());
    }

    private List<Event> getEventsByTagName(final List<Event> events, final String tag) {
        return events.stream().filter(event -> event.getTags().stream()
            .map(Tag::getTagTranslations)
            .flatMap(Collection::stream)
            .anyMatch(tagTranslation -> tag.equalsIgnoreCase(tagTranslation.getName())))
            .collect(Collectors.toList());
    }

    private List<Event> getSortedListByEventId(List<Event> unsorted) {
        return unsorted.stream().distinct().sorted(Comparator.comparing(Event::getId, Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }

    private PageableAdvancedDto<EventDto> buildPageableAdvancedDto(Page<Event> eventsPage, Long userId) {
        List<EventDto> eventDtos = modelMapper.map(eventsPage.getContent(),
            new TypeToken<List<EventDto>>() {
            }.getType());

        if (Objects.nonNull(eventDtos)) {
            eventDtos.forEach(eventDto -> {
                if (Objects.nonNull(eventDto.getOrganizer())) {
                    Long idOrganizer = eventDto.getOrganizer().getId();
                    if (Objects.nonNull(idOrganizer)) {
                        boolean isOrganizedByFriend = userRepo.isFriend(idOrganizer, userId);
                        eventDto.setOrganizedByFriend(isOrganizedByFriend);
                    } else {
                        eventDto.setOrganizedByFriend(false);
                    }
                } else {
                    eventDto.setOrganizedByFriend(false);
                }
            });
        }

        if (CollectionUtils.isNotEmpty(eventDtos)) {
            setSubscribes(eventDtos, userId);
            setFollowers(eventDtos, userId);
        }

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

    private PageableAdvancedDto<EventDto> buildPageableAdvancedDto(Page<Event> eventsPage) {
        List<EventDto> eventDtos = modelMapper.map(eventsPage.getContent(),
            new TypeToken<List<EventDto>>() {
            }.getType());

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

    private void setSubscribes(Collection<EventDto> eventDtos, Long userId) {
        List<Long> eventIds = eventDtos.stream().map(EventDto::getId).collect(Collectors.toList());
        List<Event> subscribedEvents = eventRepo.findSubscribedAmongEventIds(eventIds, userId);
        List<Long> subscribedEventIds = subscribedEvents.stream()
            .map(Event::getId)
            .collect(Collectors.toList());
        eventDtos.forEach(eventDto -> eventDto.setSubscribed(subscribedEventIds.contains(eventDto.getId())));
    }

    private void setFollowers(Collection<EventDto> eventDtos, Long userId) {
        List<Long> eventIds = eventDtos.stream().map(EventDto::getId).collect(Collectors.toList());
        List<Event> followedEvents = eventRepo.findFavoritesAmongEventIds(eventIds, userId);
        List<Long> followedEventIds = followedEvents.stream()
            .map(Event::getId)
            .collect(Collectors.toList());
        eventDtos.forEach(eventDto -> eventDto.setFavorite(followedEventIds.contains(eventDto.getId())));
    }

    private EventDto buildEventDto(Event event, Long userId) {
        EventDto eventDto = modelMapper.map(event, EventDto.class);

        setFollowers(List.of(eventDto), userId);
        setSubscribes(List.of(eventDto), userId);

        return eventDto;
    }

    private EventDto buildEventDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

    @Override
    public PageableDto<SearchEventsDto> search(String searchQuery, String languageCode) {
        Page<Event> page = eventsSearchRepo.find(PageRequest.of(0, 3), searchQuery, languageCode);
        return getSearchNewsDtoPageableDto(page);
    }

    @Override
    public PageableDto<SearchEventsDto> search(Pageable pageable, String searchQuery, String languageCode) {
        Page<Event> page = eventsSearchRepo.find(pageable, searchQuery, languageCode);
        return getSearchNewsDtoPageableDto(page);
    }

    private PageableDto<SearchEventsDto> getSearchNewsDtoPageableDto(Page<Event> page) {
        List<SearchEventsDto> searchEventsDtos = page.stream()
            .map(events -> modelMapper.map(events, SearchEventsDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            searchEventsDtos,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages());
    }

    @Override
    public Long getAmountOfOrganizedAndAttendedEventsByUserId(Long userId) {
        return eventRepo.getAmountOfOrganizedAndAttendedEventsByUserId(userId);
    }
}

package greencity.service;

import static greencity.constant.EventTupleConstant.cityEn;
import static greencity.constant.EventTupleConstant.cityUk;
import static greencity.constant.EventTupleConstant.countComments;
import static greencity.constant.EventTupleConstant.countryEn;
import static greencity.constant.EventTupleConstant.countryUk;
import static greencity.constant.EventTupleConstant.creationDate;
import static greencity.constant.EventTupleConstant.currentUserGrade;
import static greencity.constant.EventTupleConstant.description;
import static greencity.constant.EventTupleConstant.dislikes;
import static greencity.constant.EventTupleConstant.eventId;
import static greencity.constant.EventTupleConstant.finishDate;
import static greencity.constant.EventTupleConstant.formattedAddressEn;
import static greencity.constant.EventTupleConstant.formattedAddressUk;
import static greencity.constant.EventTupleConstant.grade;
import static greencity.constant.EventTupleConstant.houseNumber;
import static greencity.constant.EventTupleConstant.isFavorite;
import static greencity.constant.EventTupleConstant.isOpen;
import static greencity.constant.EventTupleConstant.isOrganizedByFriend;
import static greencity.constant.EventTupleConstant.isRelevant;
import static greencity.constant.EventTupleConstant.isSubscribed;
import static greencity.constant.EventTupleConstant.languageCode;
import static greencity.constant.EventTupleConstant.latitude;
import static greencity.constant.EventTupleConstant.likes;
import static greencity.constant.EventTupleConstant.longitude;
import static greencity.constant.EventTupleConstant.onlineLink;
import static greencity.constant.EventTupleConstant.organizerId;
import static greencity.constant.EventTupleConstant.organizerName;
import static greencity.constant.EventTupleConstant.regionEn;
import static greencity.constant.EventTupleConstant.regionUk;
import static greencity.constant.EventTupleConstant.startDate;
import static greencity.constant.EventTupleConstant.streetEn;
import static greencity.constant.EventTupleConstant.streetUk;
import static greencity.constant.EventTupleConstant.tagId;
import static greencity.constant.EventTupleConstant.tagName;
import static greencity.constant.EventTupleConstant.title;
import static greencity.constant.EventTupleConstant.titleImage;
import static greencity.constant.EventTupleConstant.type;
import static greencity.utils.SpecificationUtils.setValueIfNotEmpty;
import com.google.maps.model.LatLng;
import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.client.UserRemoteClient;
import greencity.constant.AppConstant;
import greencity.constant.ErrorMessage;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.PageableDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddressDto;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventCityDto;
import greencity.dto.event.EventDateLocationDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.EventResponseDto;
import greencity.dto.event.EventVO;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.event.UpdateEventRequestDto;
import greencity.dto.filter.FilterEventDto;
import greencity.dto.geocoding.AddressLatLngResponse;
import greencity.dto.language.LanguageDTO;
import greencity.dto.location.UserLocationDto;
import greencity.dto.notification.LikeNotificationDto;
import greencity.dto.search.SearchEventsDto;
import greencity.dto.tag.TagDto;
import greencity.dto.tag.TagUkEnDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserForListDto;
import greencity.dto.user.UserProfilePictureDto;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventGrade;
import greencity.entity.event.EventImages;
import greencity.entity.event.Event_;
import greencity.enums.AchievementAction;
import greencity.enums.AchievementCategoryType;
import greencity.enums.EventStatus;
import greencity.enums.EventType;
import greencity.enums.NotificationType;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.filters.EventIdsManagementSpecification;
import greencity.filters.EventIdsSpecification;
import greencity.filters.EventSearchSpecification;
import greencity.filters.SearchCriteria;
import greencity.mapping.events.EventDateLocationDtoMapper;
import greencity.rating.RatingCalculation;
import greencity.repository.EventRepo;
import greencity.repository.RatingPointsRepo;
import greencity.repository.UserRepo;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private static final String DEFAULT_TITLE_IMAGE_PATH = AppConstant.DEFAULT_EVENT_IMAGES;
    private final EventRepo eventRepo;
    private final ModelMapper modelMapper;
    private final EventDateLocationDtoMapper eventDateLocationDtoMapper;
    private final RestClient restClient;
    private final UserRemoteClient userRemoteClient;
    private final TagsService tagService;
    private final GoogleApiService googleApiService;
    private final UserService userService;
    private final UserRepo userRepo;
    private final RatingCalculation ratingCalculation;
    private final AchievementCalculation achievementCalculation;
    private final UserNotificationService userNotificationService;
    private final RatingPointsRepo ratingPointsRepo;

    public static List<String> getImagesLinksToDelete(List<String> existingLinks, List<String> newLinks) {
        return existingLinks.stream()
            .filter(existingLink -> !newLinks.contains(existingLink))
            .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    public EventDto save(AddEventDtoRequest addEventDtoRequest, String email, MultipartFile[] images) {
        Event savedEvent = processEventSaving(addEventDtoRequest, email, images);
        return buildEventDto(savedEvent, savedEvent.getOrganizer().getId());
    }

    /**
     * {@inheritDoc}
     */
    public EventResponseDto saveV2(AddEventDtoRequest addEventDtoRequest, String email, MultipartFile[] images) {
        Event savedEvent = processEventSaving(addEventDtoRequest, email, images);
        return buildEventResponseDto(savedEvent, savedEvent.getOrganizer().getId());
    }

    private Event processEventSaving(AddEventDtoRequest addEventDtoRequest, String email, MultipartFile[] images) {
        validateEventRequest(addEventDtoRequest);

        Event toSave = modelMapper.map(addEventDtoRequest, Event.class);
        UserVO userVO = restClient.findByEmail(email);
        User organizer = modelMapper.map(userVO, User.class);
        toSave.setOrganizer(organizer);
        toSave.setType(getEventType(toSave.getDates()));

        setEventImages(toSave, images);
        setEventTags(toSave, addEventDtoRequest.getTags());

        Event savedEvent = eventRepo.save(toSave);
        postEventSaveActions(savedEvent, userVO);

        return savedEvent;
    }

    private void setEventImages(Event event, MultipartFile[] images) {
        if (images != null && images.length > 0 && images[0] != null) {
            try {
                event.setTitleImage(userRemoteClient.uploadFile(images[0]));
            } catch (WebClientRequestException | WebClientResponseException e) {
                log.warn(AppConstant.USER_SERVICE_UNAVAILABLE_LOG, e.getMessage());
            }
            List<EventImages> eventImages = new ArrayList<>();
            for (int i = 1; i < images.length; i++) {
                if (images[i] != null) {
                    try {
                        eventImages.add(EventImages.builder().event(event).link(userRemoteClient.uploadFile(images[i]))
                            .build());
                    } catch (WebClientRequestException | WebClientResponseException e) {
                        log.warn(AppConstant.USER_SERVICE_UNAVAILABLE_LOG, e.getMessage());
                    }
                }
            }
            event.setAdditionalImages(eventImages);
        } else {
            event.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }
    }

    private void setEventTags(Event event, List<String> tagNames) {
        List<TagVO> tagVOs = tagService.findTagsWithAllTranslationsByNamesAndType(tagNames, TagType.EVENT);
        event.setTags(modelMapper.map(tagVOs, new TypeToken<List<Tag>>() {
        }.getType()));
    }

    private void postEventSaveActions(Event savedEvent, UserVO userVO) {
        achievementCalculation.calculateAchievement(userVO, AchievementCategoryType.CREATE_EVENT,
            AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("CREATE_EVENT"), userVO);
        userNotificationService.createNewNotification(userVO, NotificationType.EVENT_CREATED, savedEvent.getId(),
            savedEvent.getTitle());
    }

    private EventType getEventType(List<EventDateLocation> dates) {
        boolean hasOnlineEvent = false;
        boolean hasOfflineEvent = false;

        for (EventDateLocation date : dates) {
            if (date.getOnlineLink() != null) {
                hasOnlineEvent = true;
            }
            if (date.getAddress() != null) {
                hasOfflineEvent = true;
            }
        }

        if (hasOnlineEvent && hasOfflineEvent) {
            return EventType.ONLINE_OFFLINE;
        } else if (hasOnlineEvent) {
            return EventType.ONLINE;
        } else {
            return EventType.OFFLINE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Long eventId, String email) {
        UserVO userVO = restClient.findByEmail(email);
        Event toDelete = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        List<String> eventImages = new ArrayList<>();
        eventImages.add(toDelete.getTitleImage());
        if (toDelete.getAdditionalImages() != null) {
            eventImages.addAll(toDelete.getAdditionalImages().stream().map(EventImages::getLink)
                .toList());
        }

        if (toDelete.getOrganizer().getId().equals(userVO.getId()) || userVO.getRole() == Role.ROLE_ADMIN) {
            deleteImagesFromServer(eventImages);
            List<UserVO> userVOList = toDelete.getAttenders().stream()
                .map(user -> modelMapper.map(user, UserVO.class))
                .collect(Collectors.toList());
            userNotificationService.createNotificationForAttenders(userVOList, toDelete.getTitle(),
                NotificationType.EVENT_CANCELED, null);
            eventRepo.delete(toDelete);
        } else {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.CREATE_EVENT, AchievementAction.DELETE);
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("UNDO_CREATE_EVENT"), userVO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDto getEvent(Long eventId, Principal principal) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        if (principal != null) {
            User currentUser =
                modelMapper.map(restClient.findByEmail(principal.getName()), User.class);
            return buildEventDto(event, currentUser.getId());
        }
        return buildEventDto(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventResponseDto getEventV2(Long eventId, Principal principal) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        if (principal != null) {
            User currentUser = modelMapper.map(restClient.findByEmail(principal.getName()), User.class);
            return buildEventResponseDto(event, currentUser.getId());
        }
        return modelMapper.map(event, EventResponseDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<EventDto> getEvents(Pageable pageable, FilterEventDto filterEventDto, Long userId) {
        if (userId != null) {
            restClient.findById(userId);
        }

        List<SearchCriteria> searchCriteriaList = createEventSearchCriteria(filterEventDto);
        Specification<Event> specification = new EventIdsSpecification(searchCriteriaList, userId);
        Page<Long> eventIds = eventRepo.findAll(specification, pageable)
            .map(Event::getId);

        if (pageable.getPageNumber() >= eventIds.getTotalPages() && eventIds.getTotalPages() > 0) {
            throw new BadRequestException(
                String.format(ErrorMessage.PAGE_NOT_FOUND_MESSAGE, pageable.getPageNumber(), eventIds.getTotalPages()));
        }

        List<Tuple> tuples;
        if (userId != null) {
            tuples = eventRepo.loadEventDataByIds(eventIds.getContent(), userId);
        } else {
            tuples = eventRepo.loadEventDataByIds(eventIds.getContent());
        }
        return buildPageableAdvancedDto(eventIds, tuples, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<EventDto> getEventsManagement(Pageable pageable, FilterEventDto filterEventDto,
        Long userId) {
        if (userId != null) {
            restClient.findById(userId);
        }

        List<SearchCriteria> searchCriteriaList = createEventSearchCriteria(filterEventDto);
        Specification<Event> specification = new EventIdsManagementSpecification(searchCriteriaList, userId);
        Page<Long> eventIds = eventRepo.findAll(specification, pageable)
            .map(Event::getId);

        List<Tuple> tuples;
        if (userId != null) {
            tuples = eventRepo.loadEventDataByIds(eventIds.getContent(), userId);
        } else {
            tuples = eventRepo.loadEventDataByIds(eventIds.getContent());
        }
        return buildPageableAdvancedDto(eventIds, tuples, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAttender(Long eventId, String email) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        UserVO userVO = restClient.findByEmail(email);
        User currentUser = modelMapper.map(userVO, User.class);
        checkAttenderToJoinTheEvent(event, currentUser);
        event.getAttenders().add(currentUser);
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.JOIN_EVENT, AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("JOIN_EVENT"), userVO);
        eventRepo.save(event);
        userNotificationService.createNotification(modelMapper.map(event.getOrganizer(), UserVO.class), userVO,
            NotificationType.EVENT_JOINED, eventId, event.getTitle());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAttender(Long eventId, String email) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        UserVO userVO = restClient.findByEmail(email);
        event.setAttenders(event.getAttenders().stream().filter(user -> !user.getId().equals(userVO.getId()))
            .collect(Collectors.toSet()));
        achievementCalculation.calculateAchievement(userVO,
            AchievementCategoryType.JOIN_EVENT, AchievementAction.DELETE);
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("UNDO_JOIN_EVENT"), userVO);
        eventRepo.save(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addToFavorites(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));

        User currentUser = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        if (event.getFollowers().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_ALREADY_ADDED_EVENT_TO_FAVORITES);
        }

        event.getFollowers().add(currentUser);
        eventRepo.save(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFromFavorites(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));

        User currentUser = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        if (!event.getFollowers().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_NOT_IN_FAVORITES);
        }

        event.setFollowers(event.getAttenders()
            .stream()
            .filter(user -> !user.getId().equals(currentUser.getId()))
            .collect(Collectors.toSet()));
        eventRepo.save(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableAdvancedDto<EventDto> searchEventsBy(Pageable paging, String query) {
        Page<Event> page = eventRepo.searchEventsBy(paging, query);
        return buildPageableAdvancedDto(page);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EventDto update(UpdateEventRequestDto eventDtoRequest, String email, MultipartFile[] images) {
        Event updatedEvent = processEventUpdate(eventDtoRequest, email, images);
        return buildEventDto(updatedEvent, updatedEvent.getOrganizer().getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EventResponseDto updateV2(UpdateEventRequestDto eventDtoRequest, String email, MultipartFile[] images) {
        Event updatedEvent = processEventUpdate(eventDtoRequest, email, images);
        return buildEventResponseDto(updatedEvent, updatedEvent.getOrganizer().getId());
    }

    private Event processEventUpdate(UpdateEventRequestDto eventDtoRequest, String email, MultipartFile[] images) {
        UpdateEventDto eventDto = modelMapper.map(eventDtoRequest, UpdateEventDto.class);
        checkingEqualityDateTimeInEventDateLocationDto(eventDto.getDatesLocations());

        Event toUpdate = eventRepo.findById(eventDto.getId())
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        User organizer = modelMapper.map(restClient.findByEmail(email), User.class);
        validateOrganizerPermissions(organizer, toUpdate);
        validateEventNotFinished(toUpdate);

        notifyAttenders(toUpdate, eventDto);

        enhanceWithNewData(toUpdate, eventDto, images);

        return eventRepo.save(toUpdate);
    }

    private void validateOrganizerPermissions(User organizer, Event toUpdate) {
        UserVO organizerVO = modelMapper.map(organizer, UserVO.class);
        if (organizerVO.getRole() != Role.ROLE_ADMIN && organizerVO.getRole() != Role.ROLE_MODERATOR
            && !organizer.getId().equals(toUpdate.getOrganizer().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
    }

    private void validateEventNotFinished(Event toUpdate) {
        if (findLastEventDateTime(toUpdate).isBefore(ZonedDateTime.now())) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_FINISHED);
        }
    }

    private void notifyAttenders(Event toUpdate, UpdateEventDto eventDto) {
        List<UserVO> userVOList = toUpdate.getAttenders().stream()
            .map(user -> modelMapper.map(user, UserVO.class))
            .collect(Collectors.toList());

        if (toUpdate.getTitle().equals(eventDto.getTitle())) {
            userNotificationService.createNotificationForAttenders(userVOList, toUpdate.getTitle(),
                NotificationType.EVENT_UPDATED, toUpdate.getId());
        } else {
            userNotificationService.createNotificationForAttenders(userVOList, toUpdate.getTitle(),
                NotificationType.EVENT_NAME_UPDATED, toUpdate.getId(), eventDto.getTitle());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rateEvent(Long eventId, Long userId, int grade) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        User currentUser = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        if (event.getOrganizer().getId().equals(currentUser.getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_RIGHTS_TO_RATE_EVENT);
        }
        if (findLastEventDateTime(event).isAfter(ZonedDateTime.now())) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_NOT_FINISHED);
        }
        if (!event.getAttenders().stream().map(User::getId).toList()
            .contains(currentUser.getId())) {
            throw new BadRequestException(ErrorMessage.YOU_ARE_NOT_EVENT_SUBSCRIBER);
        }
        if (event.getEventGrades().stream().map(eventGrade -> eventGrade.getUser().getId()).toList()
            .contains(currentUser.getId())) {
            throw new BadRequestException(ErrorMessage.HAVE_ALREADY_RATED);
        }

        event.getEventGrades().add(EventGrade.builder().event(event).grade(grade).user(currentUser).build());
        eventRepo.save(event);

        userService.updateEventOrganizerRating(event.getOrganizer().getId(),
            calculateUserEventOrganizerRating(event.getOrganizer()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<EventAttenderDto> getAllEventAttenders(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));
        return event.getAttenders().stream().map(attender -> modelMapper.map(attender, EventAttenderDto.class))
            .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
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
                .map(location -> setEventToEventDateLocation(location, toUpdate))
                .collect(Collectors.toList()));
            toUpdate.setType(getEventType(toUpdate.getDates()));
        }
    }

    private EventDateLocation setEventToEventDateLocation(EventDateLocation location, Event e) {
        return location.setEvent(e);
    }

    private void updateImages(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        List<String> additionalImages = updateEventDto.getAdditionalImages();
        List<String> imagesToDelete = additionalImages == null || additionalImages.isEmpty()
            ? eventRepo.findAllImagesLinksByEventId(toUpdate.getId())
            : getImagesLinksToDelete(eventRepo.findAllImagesLinksByEventId(toUpdate.getId()), additionalImages);
        eventRepo.deleteEventAdditionalImagesByEventId(updateEventDto.getId());
        checkTitleImageInImagesToDelete(updateEventDto, imagesToDelete);
        if (ArrayUtils.isEmpty(images) && imagesToDelete.isEmpty()) {
            changeOldImagesWithoutRemovingAndAdding(toUpdate, updateEventDto);
        } else if (images == null || images.length == 0) {
            deleteOldImages(toUpdate, updateEventDto, imagesToDelete);
        } else if (imagesToDelete.isEmpty()) {
            addNewImages(toUpdate, updateEventDto, images);
        } else {
            deleteImagesFromServer(imagesToDelete);
            addNewImages(toUpdate, updateEventDto, images);
        }
    }

    private void checkTitleImageInImagesToDelete(UpdateEventDto updateEventDto, List<String> imagesToDelete) {
        String titleImage = updateEventDto.getTitleImage();

        if (imagesToDelete != null && titleImage != null) {
            imagesToDelete.remove(titleImage);
        }
    }

    private void changeOldImagesWithoutRemovingAndAdding(Event toUpdate, UpdateEventDto updateEventDto) {
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
        } else {
            toUpdate.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }
        if (updateEventDto.getAdditionalImages() != null) {
            toUpdate.setAdditionalImages(updateEventDto.getAdditionalImages().stream()
                .map(url -> EventImages.builder().event(toUpdate).link(url).build())
                .collect(Collectors.toList()));
        } else {
            toUpdate.setAdditionalImages(new ArrayList<>());
        }
    }

    private void deleteOldImages(Event toUpdate, UpdateEventDto updateEventDto, List<String> imagesToDelete) {
        deleteImagesFromServer(imagesToDelete);
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
            if (updateEventDto.getAdditionalImages() != null) {
                toUpdate.setAdditionalImages(updateEventDto.getAdditionalImages().stream()
                    .map(url -> EventImages.builder().event(toUpdate).link(url).build())
                    .collect(Collectors.toList()));
            } else {
                toUpdate.setAdditionalImages(new ArrayList<>());
            }
        } else {
            toUpdate.setTitleImage(DEFAULT_TITLE_IMAGE_PATH);
        }
    }

    private void deleteImagesFromServer(List<String> images) {
        try {
            images.stream().filter(img -> !img.equals(DEFAULT_TITLE_IMAGE_PATH)).forEach(userRemoteClient::deleteFile);
        } catch (WebClientRequestException | WebClientResponseException e) {
            log.warn(AppConstant.USER_SERVICE_UNAVAILABLE_LOG, e.getMessage());
        }
    }

    private void addNewImages(Event toUpdate, UpdateEventDto updateEventDto, MultipartFile[] images) {
        int imagesCounter = 0;
        if (updateEventDto.getTitleImage() != null) {
            toUpdate.setTitleImage(updateEventDto.getTitleImage());
        } else {
            try {
                toUpdate.setTitleImage(userRemoteClient.uploadFile(images[imagesCounter++]));
            } catch (WebClientRequestException | WebClientResponseException e) {
                log.warn(AppConstant.USER_SERVICE_UNAVAILABLE_LOG, e.getMessage());
            }
        }
        List<String> additionalImagesStr = new ArrayList<>();
        if (updateEventDto.getAdditionalImages() != null) {
            additionalImagesStr.addAll(updateEventDto.getAdditionalImages());
        }
        for (int i = imagesCounter; i < images.length; i++) {
            try {
                additionalImagesStr.add(userRemoteClient.uploadFile(images[imagesCounter++]));
            } catch (WebClientRequestException | WebClientResponseException e) {
                log.warn(AppConstant.USER_SERVICE_UNAVAILABLE_LOG, e.getMessage());
            }
        }
        if (!additionalImagesStr.isEmpty()) {
            toUpdate.setAdditionalImages(additionalImagesStr.stream().map(url -> EventImages.builder()
                .event(toUpdate).link(url).build()).collect(Collectors.toList()));
        } else {
            toUpdate.setAdditionalImages(new ArrayList<>());
        }
    }

    private void validateEventRequest(AddEventDtoRequest addEventDtoRequest) {
        checkingEqualityDateTimeInEventDateLocationDto(addEventDtoRequest.getDatesLocations());
        if (!validateCoordinates(addEventDtoRequest.getDatesLocations())) {
            throw new BadRequestException(ErrorMessage.INVALID_COORDINATES);
        }
        addAddressToLocation(addEventDtoRequest.getDatesLocations());
    }

    private boolean isValidCoordinate(double latitude, double longitude) {
        return Math.abs(latitude) <= 90 && Math.abs(longitude) <= 180;
    }

    public boolean validateCoordinates(List<EventDateLocationDto> eventDateLocationDtos) {
        for (EventDateLocationDto eventDateLocationDto : eventDateLocationDtos) {
            AddressDto coordinates = eventDateLocationDto.getCoordinates();
            EventType eventType = getEventType(eventDateLocationDtoMapper.mapAllToList(eventDateLocationDtos));

            if (EventType.ONLINE == eventType) {
                return true;
            }
            if (Objects.isNull(coordinates) || Objects.isNull(coordinates.getLatitude())
                || Objects.isNull(coordinates.getLongitude())) {
                return false;
            }

            double latitude = coordinates.getLatitude();
            double longitude = coordinates.getLongitude();

            if (!isValidCoordinate(latitude, longitude)) {
                return false;
            }
        }
        return true;
    }

    private void addAddressToLocation(List<EventDateLocationDto> eventDateLocationDtos) {
        eventDateLocationDtos.stream()
            .filter(eventDateLocationDto -> Objects.nonNull(eventDateLocationDto.getCoordinates()))
            .forEach(eventDateLocationDto -> {
                AddressDto addressDto = eventDateLocationDto.getCoordinates();
                AddressLatLngResponse response = googleApiService.getResultFromGeoCodeByCoordinates(
                    new LatLng(addressDto.getLatitude(), addressDto.getLongitude()));
                eventDateLocationDto.setCoordinates(modelMapper.map(response, AddressDto.class));
            });
    }

    private ZonedDateTime findLastEventDateTime(Event event) {
        return Collections.max(event.getDates().stream().map(EventDateLocation::getFinishDate).toList());
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

    private PageableAdvancedDto<EventDto> buildPageableAdvancedDto(Page<Long> eventIds, List<Tuple> tuples,
        Pageable pageable) {
        return new PageableAdvancedDto<>(
            mapTupleListToEventDtoList(tuples, eventIds.toList()),
            eventIds.getTotalElements(),
            pageable.getPageNumber(),
            eventIds.getTotalPages(),
            eventIds.getNumber(),
            eventIds.hasPrevious(),
            eventIds.hasNext(),
            eventIds.isFirst(),
            eventIds.isLast());
    }

    private void setSubscribes(Collection<EventDto> eventDtos, Long userId) {
        List<Long> eventIds = eventDtos.stream().map(EventDto::getId).collect(Collectors.toList());
        List<Event> subscribedEvents = eventRepo.findSubscribedAmongEventIds(eventIds, userId);
        List<Long> subscribedEventIds = subscribedEvents.stream()
            .map(Event::getId)
            .toList();
        eventDtos.forEach(eventDto -> eventDto.setSubscribed(subscribedEventIds.contains(eventDto.getId())));
    }

    private void setFollowers(Collection<EventDto> eventDtos, Long userId) {
        List<Long> eventIds = eventDtos.stream().map(EventDto::getId).collect(Collectors.toList());
        List<Event> followedEvents = eventRepo.findFavoritesAmongEventIds(eventIds, userId);
        List<Long> followedEventIds = followedEvents.stream()
            .map(Event::getId)
            .toList();
        eventDtos.forEach(eventDto -> eventDto.setFavorite(followedEventIds.contains(eventDto.getId())));
    }

    private void setSubscribersV2(Collection<EventResponseDto> eventResponses, Long userId) {
        List<Long> eventIds = eventResponses.stream().map(EventResponseDto::id).toList();
        List<Long> subscribedEventIds = eventRepo.findSubscribedAmongEventIds(eventIds, userId)
            .stream()
            .map(Event::getId)
            .toList();

        eventResponses.forEach(eventDto -> {
            boolean isSubscribed = subscribedEventIds.contains(eventDto.id());
            updateSubscriptionStatus(eventDto, isSubscribed);
        });
    }

    private void setFollowersV2(Collection<EventResponseDto> eventResponses, Long userId) {
        List<Long> eventIds = eventResponses.stream().map(EventResponseDto::id).toList();
        List<Long> followedEventIds = eventRepo.findFavoritesAmongEventIds(eventIds, userId)
            .stream()
            .map(Event::getId)
            .toList();

        eventResponses.forEach(eventDto -> {
            boolean isSubscribed = followedEventIds.contains(eventDto.id());
            updateFavoriteStatus(eventDto, isSubscribed);
        });
    }

    private void updateSubscriptionStatus(EventResponseDto eventDto, boolean isSubscribed) {
        eventDto.withIsSubscribed(isSubscribed);
    }

    private void updateFavoriteStatus(EventResponseDto eventDto, boolean isFavorite) {
        eventDto.withIsFavorite(isFavorite);
    }

    private EventResponseDto buildEventResponseDto(Event event, Long userId) {
        EventResponseDto eventResponseDto = modelMapper.map(event, EventResponseDto.class);
        Integer currentUserGrade = event.getEventGrades()
            .stream()
            .filter(g -> g.getUser() != null && g.getUser().getId().equals(userId))
            .map(EventGrade::getGrade)
            .findFirst()
            .orElse(null);

        setFollowersV2(List.of(eventResponseDto), userId);
        setSubscribersV2(List.of(eventResponseDto), userId);

        return new EventResponseDto(
            eventResponseDto.id(),
            eventResponseDto.eventInformation(),
            eventResponseDto.organizer(),
            eventResponseDto.creationDate(),
            eventResponseDto.isOpen(),
            eventResponseDto.dates(),
            eventResponseDto.titleImage(),
            eventResponseDto.additionalImages(),
            eventResponseDto.type(),
            eventResponseDto.isSubscribed(),
            eventResponseDto.isFavorite(),
            eventResponseDto.isRelevant(),
            eventResponseDto.likes(),
            eventResponseDto.dislikes(),
            eventResponseDto.countComments(),
            eventResponseDto.isOrganizedByFriend(),
            eventResponseDto.eventRate(),
            currentUserGrade);
    }

    private EventDto buildEventDto(Event event, Long userId) {
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        Integer currentUserGrade = event.getEventGrades()
            .stream()
            .filter(g -> g.getUser() != null && g.getUser().getId().equals(userId))
            .map(EventGrade::getGrade)
            .findFirst()
            .orElse(null);
        setFollowers(List.of(eventDto), userId);
        setSubscribes(List.of(eventDto), userId);
        eventDto.setCurrentUserGrade(currentUserGrade);
        return eventDto;
    }

    private EventDto buildEventDto(Event event) {
        return modelMapper.map(event, EventDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageableDto<SearchEventsDto> search(Pageable pageable, String searchQuery, Boolean isFavorite, Long userId) {
        List<SearchCriteria> searchCriteriaList = new ArrayList<>();
        setValueIfNotEmpty(searchCriteriaList, "text", searchQuery);
        setValueIfNotEmpty(searchCriteriaList, "isFavorite", isFavorite);

        Specification<Event> specification = new EventSearchSpecification(searchCriteriaList, userId);
        Page<Event> events = eventRepo.findAll(specification, pageable);
        return getSearchNewsDtoPageableDto(events);
    }

    private PageableDto<SearchEventsDto> getSearchNewsDtoPageableDto(Page<Event> page) {
        List<SearchEventsDto> searchEventsDtos = page.stream()
            .map(event -> modelMapper.map(event, SearchEventsDto.class))
            .toList();

        return new PageableDto<>(
            searchEventsDtos,
            page.getTotalElements(),
            page.getPageable().getPageNumber(),
            page.getTotalPages());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AddressDto> getAllEventsAddresses() {
        return eventRepo.findAllEventsAddresses().stream()
            .map(eventAddress -> modelMapper.map(eventAddress, AddressDto.class))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getCountOfAttendedEventsByUserId(Long userId) {
        return eventRepo.countDistinctByAttendersId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getCountOfAttendedEventsByEmail(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return eventRepo.countDistinctByAttendersId(user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getCountOfOrganizedEventsByUserId(Long userId) {
        return eventRepo.countDistinctByOrganizerId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getCountOfOrganizedEventsByEmail(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_EMAIL + email));
        return eventRepo.countDistinctByOrganizerId(user.getId());
    }

    @Override
    public void like(Long eventId, UserVO userVO) {
        likeMethodHelper(eventId, userVO);
    }

    @Override
    public void dislike(UserVO userVO, Long eventId) {
        dislikeMethodHelper(eventId, userVO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countLikes(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return event.getUsersLikedEvents().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countDislikes(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return event.getUsersDislikedEvents().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEventLikedByUser(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return event.getUsersLikedEvents().stream().anyMatch(u -> u.getId().equals(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEventDislikedByUser(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return event.getUsersDislikedEvents().stream().anyMatch(u -> u.getId().equals(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<UserProfilePictureDto> getUsersLikedByEvent(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return event.getUsersLikedEvents().stream().map(u -> modelMapper.map(u, UserProfilePictureDto.class))
            .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<UserProfilePictureDto> getUsersDislikedByEvent(Long eventId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));
        return event.getUsersDislikedEvents().stream().map(u -> modelMapper.map(u, UserProfilePictureDto.class))
            .collect(Collectors.toSet());
    }

    @Override
    public void addToRequested(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));

        User currentUser = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        if (event.getRequesters().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.USER_HAS_ALREADY_ADDED_EVENT_TO_REQUESTED);
        }

        event.getRequesters().add(currentUser);
        eventRepo.save(event);

        userNotificationService.createNotification(modelMapper.map(event.getOrganizer(), UserVO.class),
            modelMapper.map(currentUser, UserVO.class), NotificationType.EVENT_INVITE, eventId, event.getTitle());
    }

    @Override
    public void removeFromRequested(Long eventId, Long userId) {
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + eventId));

        User currentUser = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        if (!event.getRequesters().contains(currentUser)) {
            throw new BadRequestException(ErrorMessage.EVENT_IS_NOT_IN_REQUESTED);
        }

        event.getRequesters().remove(currentUser);
        eventRepo.save(event);
    }

    @Override
    public PageableDto<UserForListDto> getRequestedUsers(Long eventId, Long userId, Pageable pageable) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        if (!user.equals(event.getOrganizer())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }

        Page<User> usersPage = userRepo.findUsersByRequestedEvents(eventId, pageable);
        List<UserForListDto> userList = usersPage.stream()
            .map(users -> modelMapper.map(users, UserForListDto.class))
            .collect(Collectors.toList());

        return new PageableDto<>(
            userList,
            usersPage.getTotalElements(),
            usersPage.getPageable().getPageNumber(),
            usersPage.getTotalPages());
    }

    @Override
    public void approveRequest(Long eventId, String email, Long userId) {
        UserVO userVO = restClient.findByEmail(email);
        User currentUser = modelMapper.map(userVO, User.class);

        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        if (!Objects.equals(currentUser.getId(), event.getOrganizer().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (event.getRequesters().stream().noneMatch(u -> Objects.equals(u.getId(), userId))) {
            throw new BadRequestException(ErrorMessage.USER_DID_NOT_REQUEST_FOR_EVENT + userId);
        }
        User userToJoin = userRepo.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        event.getRequesters().remove(userToJoin);
        event.getAttenders().add(userToJoin);

        eventRepo.save(event);

        userNotificationService.createNotification(modelMapper.map(userToJoin, UserVO.class), userVO,
            NotificationType.EVENT_REQUEST_ACCEPTED, eventId, event.getTitle());
    }

    @Override
    public void declineRequest(Long eventId, String email, Long userId) {
        UserVO userVO = restClient.findByEmail(email);
        User currentUser = modelMapper.map(userVO, User.class);
        Event event = eventRepo.findById(eventId)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND));

        if (!Objects.equals(currentUser.getId(), event.getOrganizer().getId())) {
            throw new UserHasNoPermissionToAccessException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (event.getRequesters().stream().noneMatch(u -> Objects.equals(u.getId(), userId))) {
            throw new BadRequestException(ErrorMessage.USER_DID_NOT_REQUEST_FOR_EVENT + userId);
        }
        User userToJoin =
            userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId));

        event.getRequesters().remove(userToJoin);
        eventRepo.save(event);

        userNotificationService.createNotification(modelMapper.map(userToJoin, UserVO.class), userVO,
            NotificationType.EVENT_REQUEST_DECLINED, eventId, event.getTitle());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<EventAttenderDto> getAttendersPage(Long eventId, Pageable pageable) {
        return eventRepo.getAttendersPageByEventId(eventId, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<UserProfilePictureDto> getUsersLikedEventPage(Long eventId, Pageable pageable) {
        return eventRepo.getUsersLikedEventProfilePicturesPage(eventId, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<UserProfilePictureDto> getUsersDislikedEventPage(Long eventId, Pageable pageable) {
        return eventRepo.getUsersDislikedEventProfilePicturesPage(eventId, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDto> getAllEventsOrganizedByUser(Long userId) {
        checkUserIdNotNull(userId);

        List<Event> userEvents = handleEmptyEvents(eventRepo.findAllUserEventsByUserId(userId), userId);

        return userEvents.stream()
            .filter(event -> event.getOrganizer().getId().equals(userId))
            .map(event -> buildEventDto(event, userId))
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDto> getAllEventsAttendedByUser(Long userId) {
        checkUserIdNotNull(userId);

        List<Event> attendedEvents = handleEmptyEvents(eventRepo.findAllAttendedEventsByUserId(userId), userId);

        return attendedEvents.stream()
            .map(event -> buildEventDto(event, userId))
            .toList();
    }

    private void checkUserIdNotNull(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_ID_NULL);
        }
    }

    private List<Event> handleEmptyEvents(List<Event> events, Long userId) {
        return Optional.ofNullable(events)
            .filter(list -> !list.isEmpty())
            .orElseGet(() -> {
                if (!userRepo.existsById(userId)) {
                    throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
                }
                return Collections.emptyList();
            });
    }

    private void sendEventLikeNotification(User targetUser, UserVO actionUser, Long eventId, Event event) {
        final LikeNotificationDto likeNotificationDto = LikeNotificationDto.builder()
            .targetUserVO(modelMapper.map(targetUser, UserVO.class))
            .actionUserVO(actionUser)
            .newsId(eventId)
            .newsTitle(event.getTitle())
            .notificationType(NotificationType.EVENT_LIKE)
            .secondMessageText(event.getTitle())
            .isLike(true)
            .build();
        userNotificationService.createOrUpdateLikeNotification(likeNotificationDto);
    }

    private List<EventDto> mapTupleListToEventDtoList(List<Tuple> page, List<Long> sortedIds) {
        Map<Long, EventDto> eventsMap = new HashMap<>();
        Map<Long, Set<TagDto>> tagsMap = new HashMap<>();
        List<EventDto> sortedDtos = new ArrayList<>();
        for (Tuple tuple : page) {
            long id = tuple.get(eventId, Long.class);
            EventDto eventDto;
            if (!eventsMap.containsKey(id)) {
                eventDto = EventDto.builder()
                    .id(id)
                    .title(tuple.get(title, String.class))
                    .description(tuple.get(description, String.class))
                    .organizer(EventAuthorDto.builder()
                        .id(tuple.get(organizerId, Long.class))
                        .name(tuple.get(organizerName, String.class))
                        .build())
                    .creationDate(tuple.get(creationDate, Date.class).toLocalDate())
                    .titleImage(tuple.get(titleImage, String.class))
                    .isOpen(tuple.get(isOpen, Boolean.class))
                    .type(EventType.valueOf(tuple.get(type, String.class)))
                    .isRelevant(tuple.get(isRelevant, Boolean.class))
                    .likes(Math.toIntExact(tuple.get(likes, Long.class)))
                    .dislikes(Math.toIntExact(tuple.get(dislikes, Long.class)))
                    .countComments(Math.toIntExact(tuple.get(countComments, Long.class)))
                    .isOrganizedByFriend(tuple.get(isOrganizedByFriend, Boolean.class))
                    .isFavorite(tuple.get(isFavorite, Boolean.class))
                    .isSubscribed(tuple.get(isSubscribed, Boolean.class))
                    .eventRate(tuple.get(grade, BigDecimal.class) != null
                        ? tuple.get(grade, BigDecimal.class).doubleValue()
                        : 0.0)
                    .currentUserGrade(tuple.get(currentUserGrade, Integer.class))
                    .dates(new ArrayList<>())
                    .tags(new ArrayList<>())
                    .build();
                eventsMap.put(id, eventDto);
            } else {
                eventDto = eventsMap.get(id);
            }
            AddressDto addressDto = AddressDto.builder()
                .latitude(tuple.get(latitude, Double.class))
                .longitude(tuple.get(longitude, Double.class))
                .streetEn(tuple.get(streetEn, String.class))
                .streetUk(tuple.get(streetUk, String.class))
                .houseNumber(tuple.get(houseNumber, String.class))
                .cityEn(tuple.get(cityEn, String.class))
                .cityUk(tuple.get(cityUk, String.class))
                .regionEn(tuple.get(regionEn, String.class))
                .regionUk(tuple.get(regionUk, String.class))
                .countryEn(tuple.get(countryEn, String.class))
                .countryUk(tuple.get(countryUk, String.class))
                .formattedAddressEn(tuple.get(formattedAddressEn, String.class))
                .formattedAddressUk(tuple.get(formattedAddressUk, String.class))
                .build();
            if (ObjectUtils.allNull(addressDto.getLatitude(), addressDto.getLongitude(), addressDto.getStreetEn(),
                addressDto.getStreetUk(), addressDto.getCityEn(), addressDto.getCityUk(), addressDto.getCountryEn(),
                addressDto.getCountryUk(), addressDto.getRegionEn(), addressDto.getRegionUk(),
                addressDto.getHouseNumber(), addressDto.getFormattedAddressEn(), addressDto.getFormattedAddressUk())) {
                addressDto = null;
            }
            eventDto.getDates().add(EventDateLocationDto.builder()
                .startDate(ZonedDateTime.ofInstant(tuple.get(startDate, Instant.class), ZoneId.systemDefault()))
                .finishDate(ZonedDateTime.ofInstant(tuple.get(finishDate, Instant.class), ZoneId.systemDefault()))
                .onlineLink(tuple.get(onlineLink, String.class))
                .coordinates(addressDto)
                .build());
            Set<TagDto> tagDtos = tagsMap.getOrDefault(id, new HashSet<>());
            tagDtos.add(TagDto.builder()
                .id(tuple.get(tagId, Long.class))
                .name(tuple.get(tagName, String.class))
                .languageCode(tuple.get(languageCode, String.class))
                .build());
            tagsMap.put(id, tagDtos);
        }
        for (Long id : sortedIds) {
            EventDto eventDto = eventsMap.get(id);
            Set<TagDto> tags = tagsMap.get(id);
            List<TagUkEnDto> tagUaEnDtos = new ArrayList<>();

            Map<Long, List<TagDto>> groupedTags = tags.stream()
                .collect(Collectors.groupingBy(TagDto::getId));

            groupedTags.forEach((tagId, tagList) -> {
                Map<String, TagDto> uaEnMap = new HashMap<>();
                tagList.stream()
                    .filter(tag -> !uaEnMap.containsKey(tag.getLanguageCode()))
                    .forEach(tag -> uaEnMap.put(tag.getLanguageCode(), tag));
                if (uaEnMap.containsKey("uk") && uaEnMap.containsKey("en")) {
                    TagUkEnDto tagUaEnDto = TagUkEnDto.builder()
                        .id(tagId)
                        .nameUk(uaEnMap.get("uk").getName())
                        .nameEn(uaEnMap.get("en").getName())
                        .build();
                    tagUaEnDtos.add(tagUaEnDto);
                }
            });
            eventDto.setTags(tagUaEnDtos);
            sortedDtos.add(eventDto);
        }
        sortedDtos.forEach(event -> {
            List<EventDateLocationDto> uniqueDates = event.getDates().stream()
                .distinct()
                .toList();
            event.setDates(uniqueDates);
        });
        return sortedDtos;
    }

    private void checkingEqualityDateTimeInEventDateLocationDto(List<EventDateLocationDto> eventDateLocationDtos) {
        if (eventDateLocationDtos != null && !eventDateLocationDtos.isEmpty()) {
            eventDateLocationDtos.stream()
                .filter(eventDateLocationDto -> Duration.between(
                    eventDateLocationDto.getStartDate(),
                    eventDateLocationDto.getFinishDate()).toMinutes() < 30)
                .findAny()
                .ifPresent(eventDateLocationDto -> {
                    throw new IllegalArgumentException(ErrorMessage.INVALID_DURATION_BETWEEN_START_AND_FINISH);
                });
        }
    }

    private Event findEventId(Long id) {
        return eventRepo.findById(id)
            .orElseThrow(() -> new NotFoundException(ErrorMessage.EVENT_NOT_FOUND_BY_ID + id));
    }

    private User getEventAuthor(Event event) {
        if (event.getOrganizer() != null) {
            return userRepo.findById(event.getOrganizer().getId()).orElseThrow(
                () -> new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + event.getOrganizer().getId()));
        }
        return null;
    }

    /**
     * Removes a like from the event if the user has already liked it. Returns true
     * if a like was removed, false otherwise.
     */
    private boolean removeLikeIfExists(Event event, UserVO userVO, User eventAuthor) {
        boolean userLiked = event.getUsersLikedEvents().stream()
            .anyMatch(user -> user.getId().equals(userVO.getId()));

        if (userLiked) {
            event.getUsersLikedEvents().removeIf(user -> user.getId().equals(userVO.getId()));
            achievementCalculation.calculateAchievement(userVO, AchievementCategoryType.LIKE_EVENT,
                AchievementAction.DELETE);
            ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("UNDO_LIKE_EVENT"), userVO);

            if (eventAuthor != null) {
                userNotificationService.removeActionUserFromNotification(
                    modelMapper.map(eventAuthor, UserVO.class), userVO, event.getId(), NotificationType.EVENT_LIKE);
            }
            return true;
        }
        return false;
    }

    /**
     * Removes a dislike from the event if the user has already disliked it. Returns
     * true if a dislike was removed, false otherwise.
     */
    private boolean removeDislikeIfExists(Event event, UserVO userVO) {
        boolean userDisliked = event.getUsersDislikedEvents().stream()
            .anyMatch(user -> user.getId().equals(userVO.getId()));

        if (userDisliked) {
            event.getUsersDislikedEvents().removeIf(user -> user.getId().equals(userVO.getId()));
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public EventDto likeV2(Long id, UserVO user) {
        return likeMethodHelper(id, user);
    }

    @Override
    public EventDto dislikeV2(Long id, UserVO user) {
        return dislikeMethodHelper(id, user);
    }

    private EventDto likeMethodHelper(Long id, UserVO user) {
        Event event = findEventId(id);
        User eventAuthor = getEventAuthor(event);
        boolean isAuthor = Objects.nonNull(event.getOrganizer()) && event.getOrganizer().getId().equals(user.getId());
        if (isAuthor) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        if (removeLikeIfExists(event, user, eventAuthor)) {
            return modelMapper.map(event, EventDto.class);
        }
        removeDislikeIfExists(event, user);
        event.getUsersLikedEvents().add(modelMapper.map(user, User.class));
        achievementCalculation.calculateAchievement(user, AchievementCategoryType.LIKE_EVENT,
            AchievementAction.ASSIGN);
        ratingCalculation.ratingCalculation(ratingPointsRepo.findByNameOrThrow("LIKE_EVENT"), user);
        sendEventLikeNotification(eventAuthor, user, id, event);
        eventRepo.save(event);
        return modelMapper.map(event, EventDto.class);
    }

    private EventDto dislikeMethodHelper(Long id, UserVO user) {
        Event event = findEventId(id);
        boolean isAuthor = Objects.nonNull(event.getOrganizer()) && event.getOrganizer().getId().equals(user.getId());

        if (isAuthor) {
            throw new BadRequestException(ErrorMessage.USER_HAS_NO_PERMISSION);
        }
        removeLikeIfExists(event, user, getEventAuthor(event));
        if (removeDislikeIfExists(event, user)) {
            eventRepo.save(event);
            return modelMapper.map(event, EventDto.class);
        }
        event.getUsersDislikedEvents().add(modelMapper.map(user, User.class));
        eventRepo.save(event);
        return modelMapper.map(event, EventDto.class);
    }

    public Page<EventResponseDto> getPageableAllEventsAttendedByUser(Pageable pageable, Long userId) {
        List<EventResponseDto> eventResponseDtoList = eventRepo.findAllAttendedEventsByUserIdPageable(pageable, userId)
            .stream().map(event -> modelMapper.map(event, EventResponseDto.class)).toList();
        return new PageImpl<>(eventResponseDtoList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventCityDto> getAllRelevantEventsCityByUser(UserVO userVO) {
        LanguageDTO language = userVO.getLanguageVO();

        String userLocale = language.getCode();
        String userCity = AppConstant.EMPTY_STRING;
        UserLocationDto locationDto = userVO.getUserLocation();
        if (locationDto != null) {
            if (AppConstant.DEFAULT_LANGUAGE_CODE.equals(userLocale)) {
                userCity = locationDto.getCityEn() != null ? locationDto.getCityEn() : userCity;
            } else {
                userCity = locationDto.getCityUk() != null ? locationDto.getCityUk() : userCity;
            }
        }
        return eventRepo.findRelevantCitiesForUser(userCity).stream()
            .map(eventCityDtoProjection -> EventCityDto.builder()
                .cityEn(eventCityDtoProjection.getCityNameEn())
                .cityUk(eventCityDtoProjection.getCityNameUk())
                .amountOfEvents(eventCityDtoProjection.getAmountOfEvents())
                .build())
            .toList();
    }

    private List<SearchCriteria> createEventSearchCriteria(FilterEventDto filter) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        setValueIfNotEmpty(criteriaList, "eventTime", filter.getTime());
        if (filter.getCities() != null) {
            setValueIfNotEmpty(criteriaList, "cities", filter.getCities().toArray(new String[0]));
        }
        if (filter.getStatuses() != null) {
            setValueIfNotEmpty(criteriaList, "statuses", filter.getStatuses().toArray(new EventStatus[0]));
        }
        if (filter.getTags() != null) {
            setValueIfNotEmpty(criteriaList, Event_.TAGS, filter.getTags().toArray(new String[0]));
        }
        setValueIfNotEmpty(criteriaList, Event_.TITLE, filter.getTitle());
        setValueIfNotEmpty(criteriaList, "dateRange", new ZonedDateTime[] {filter.getFrom(), filter.getTo()});
        return criteriaList;
    }
}

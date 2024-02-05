package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.achievement.AchievementCalculation;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventAuthorDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.event.AddressDto;
import greencity.dto.filter.FilterEventDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.Address;
import greencity.entity.event.Event;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImages;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
import greencity.exception.exceptions.UserHasNoPermissionToAccessException;
import greencity.message.GeneralEmailMessage;
import greencity.rating.RatingCalculation;
import greencity.repository.AchievementCategoryRepo;
import greencity.repository.EventRepo;
import greencity.repository.UserRepo;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import static greencity.ModelUtils.TEST_USER_VO;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static greencity.ModelUtils.getCloseEvent;
import static greencity.ModelUtils.getFilterEventDtoWithOpenStatus;
import static greencity.ModelUtils.getPrincipal;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class EventServiceImplTest {
    @Mock
    ModelMapper modelMapper;

    @Mock
    EventRepo eventRepo;

    @Mock
    RestClient restClient;

    @Mock
    TagsService tagService;

    @Mock
    UserService userService;

    @Mock
    FileService fileService;

    @Mock
    GoogleApiService googleApiService;

    @Mock
    UserRepo userRepo;

    @InjectMocks
    EventServiceImpl eventService;
    @Mock
    private AchievementCalculation achievementCalculation;
    @Mock
    private RatingCalculation ratingCalculation;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private AchievementCategoryRepo achievementCategoryRepo;

    @Mock
    private NotificationService notificationService;

    @Test
    void save() {
        EventDto eventDto = ModelUtils.getEventDto();
        List<Long> eventIds = List.of(eventDto.getId());
        AddEventDtoRequest addEventDtoRequest = ModelUtils.addEventDtoRequest;
        Event event = ModelUtils.getEvent();
        List<Tag> tags = ModelUtils.getEventTags();
        User user = ModelUtils.getUser();

        when(modelMapper.map(addEventDtoRequest, Event.class)).thenReturn(event);
        when(restClient.findByEmail(anyString())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS))).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(tags);
        when(googleApiService.getResultFromGeoCodeByCoordinates(any()))
            .thenReturn(ModelUtils.getAddressLatLngResponse());
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of(event));
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of());

        EventDto resultEventDto = eventService.save(addEventDtoRequest, user.getEmail(), null);
        assertEquals(eventDto, resultEventDto);
        assertFalse(resultEventDto.isSubscribed());
        assertTrue(resultEventDto.isFavorite());

        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());

        MultipartFile multipartFile = ModelUtils.getMultipartFile();
        when(fileService.upload(multipartFile)).thenReturn("/url1");
        assertEquals(eventDto,
            eventService.save(addEventDtoRequest, user.getEmail(),
                new MultipartFile[] {multipartFile}));

        MultipartFile[] multipartFiles = ModelUtils.getMultipartFiles();
        when(fileService.upload(multipartFiles[0])).thenReturn("/url1");
        when(fileService.upload(multipartFiles[1])).thenReturn("/url2");
        assertEquals(eventDto,
            eventService.save(addEventDtoRequest, ModelUtils.getUser().getEmail(), multipartFiles));
    }

    @Test
    void saveEventWithoutAddress() {
        User user = ModelUtils.getUser();
        EventDto eventDtoWithoutCoordinatesDto = ModelUtils.getEventDtoWithoutAddress();
        List<Long> eventIds = List.of(eventDtoWithoutCoordinatesDto.getId());
        AddEventDtoRequest addEventDtoWithoutCoordinates = ModelUtils.addEventDtoWithoutAddressRequest;
        Event eventWithoutCoordinates = ModelUtils.getEventWithoutAddress();
        List<Tag> tags = ModelUtils.getEventTags();

        when(modelMapper.map(addEventDtoWithoutCoordinates, Event.class)).thenReturn(eventWithoutCoordinates);
        when(restClient.findByEmail(user.getEmail())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.save(eventWithoutCoordinates)).thenReturn(eventWithoutCoordinates);
        when(modelMapper.map(eventWithoutCoordinates, EventDto.class)).thenReturn(eventDtoWithoutCoordinatesDto);
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsWithAllTranslationsByNamesAndType(addEventDtoWithoutCoordinates.getTags(),
            TagType.EVENT)).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(tags);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of(eventWithoutCoordinates));
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId()))
            .thenReturn(List.of(eventWithoutCoordinates));

        EventDto resultEventDto = eventService.save(addEventDtoWithoutCoordinates, user.getEmail(), null);

        assertEquals(eventDtoWithoutCoordinatesDto, resultEventDto);
        assertTrue(resultEventDto.isSubscribed());
        assertTrue(resultEventDto.isFavorite());

        verify(restClient).findByEmail(user.getEmail());
        verify(eventRepo).save(eventWithoutCoordinates);
        verify(tagService).findTagsWithAllTranslationsByNamesAndType(addEventDtoWithoutCoordinates.getTags(),
            TagType.EVENT);
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void update() {
        EventDto eventDto = ModelUtils.getEventDto();
        Event expectedEvent = ModelUtils.getEvent();
        List<Long> eventIds = List.of(eventDto.getId());
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        User user = ModelUtils.getUser();

        when(eventRepo.findById(1L)).thenReturn(Optional.of(expectedEvent));
        when(restClient.findByEmail(anyString())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of());
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of(expectedEvent));
        when(modelMapper.map(expectedEvent, EventDto.class)).thenReturn(eventDto);
        when(eventRepo.save(expectedEvent)).thenReturn(expectedEvent);

        EventDto actualEvent = eventService.update(eventToUpdateDto, ModelUtils.getUser().getEmail(), null);

        assertEquals(eventDto, actualEvent);

        assertFalse(actualEvent.isFavorite());
        assertTrue(actualEvent.isSubscribed());

        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
        verify(restClient).findByEmail(anyString());
        await().atMost(5, SECONDS)
            .untilAsserted(() -> restClient.sendEmailNotification(any(GeneralEmailMessage.class)));
    }

    @Test
    void updateThrowsUserHasNoPermissionToAccessException() {
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        UserVO userVO = ModelUtils.getTestUserVo();
        User user = ModelUtils.getTestUser();
        String userVoEmail = userVO.getEmail();
        Event expectedEvent = ModelUtils.getEvent();

        when(eventRepo.findById(1L)).thenReturn(Optional.of(expectedEvent));
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(restClient.findByEmail(anyString())).thenReturn(userVO);

        assertThrows(UserHasNoPermissionToAccessException.class,
            () -> eventService.update(eventToUpdateDto, userVoEmail, null));

        verify(eventRepo).findById(1L);
        verify(restClient).findByEmail("user@email.com");
        verify(modelMapper).map(userVO, User.class);
        verify(eventRepo, never()).findFavoritesAmongEventIds(anyList(), anyLong());
        verify(eventRepo, never()).findSubscribedAmongEventIds(anyList(), anyLong());
    }

    @Test
    void updateFinishedEvent() {
        Event actualEvent = ModelUtils.getEventWithFinishedDate();
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        String userEmail = ModelUtils.getUser().getEmail();

        when(eventRepo.findById(any())).thenReturn(Optional.of(actualEvent));
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(restClient.findByEmail(anyString())).thenReturn(TEST_USER_VO);

        assertThrows(BadRequestException.class,
            () -> eventService.update(eventToUpdateDto, userEmail, null));

        verify(eventRepo, never()).findFavoritesAmongEventIds(anyList(), anyLong());
        verify(eventRepo, never()).findSubscribedAmongEventIds(anyList(), anyLong());
    }

    @Test
    @SneakyThrows
    void enhanceWithNewData() {
        Method method = EventServiceImpl.class.getDeclaredMethod("enhanceWithNewData", Event.class,
            UpdateEventDto.class, MultipartFile[].class);
        method.setAccessible(true);
        Event event = ModelUtils.getEvent();
        Event expectedEvent = ModelUtils.getExpectedEvent();
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(event.getTitleImage(), expectedEvent.getTitleImage());

        eventToUpdateDto.setTitle("New title");
        eventToUpdateDto.setDescription("New description");
        eventToUpdateDto.setIsOpen(false);
        eventToUpdateDto.setTags(ModelUtils.getUpdatedEventTags());
        eventToUpdateDto.setDatesLocations(ModelUtils.getUpdatedEventDateLocationDto());

        expectedEvent.setTitle("New title");
        expectedEvent.setDescription("New description");
        expectedEvent.setOpen(false);
        expectedEvent.setTags(ModelUtils.getEventTags());
        expectedEvent.setDates(List.of(ModelUtils.getUpdatedEventDateLocation()));

        List<TagVO> updatedTagVO = List.of(ModelUtils.getTagVO());
        when(tagService.findTagsWithAllTranslationsByNamesAndType(eventToUpdateDto.getTags(), TagType.EVENT))
            .thenReturn(updatedTagVO);
        when(modelMapper.map(updatedTagVO, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(ModelUtils.getEventTags());
        doNothing().when(eventRepo).deleteEventDateLocationsByEventId(1L);
        when(modelMapper.map(eventToUpdateDto.getDatesLocations().get(0), EventDateLocation.class))
            .thenReturn(ModelUtils.getUpdatedEventDateLocation());

        when(googleApiService.getResultFromGeoCodeByCoordinates(any()))
            .thenReturn(ModelUtils.getAddressLatLngResponse());

        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(event.getTitleImage(), expectedEvent.getTitleImage());
        assertEquals(event.getDescription(), expectedEvent.getDescription());
        assertEquals(event.getTags(), expectedEvent.getTags());

        eventToUpdateDto.setTitleImage("New img");
        eventToUpdateDto.setAdditionalImages(List.of("New addition image"));
        expectedEvent.setTitleImage("New img");
        expectedEvent.setAdditionalImages(List.of(EventImages.builder().link("New addition image").build()));

        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(expectedEvent.getAdditionalImages().get(0).getLink(),
            event.getAdditionalImages().get(0).getLink());
        assertEquals(event.getTitleImage(), expectedEvent.getTitleImage());

        eventToUpdateDto.setImagesToDelete(List.of("New addition image"));
        doNothing().when(fileService).delete(any());

        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(expectedEvent.getTitleImage(), event.getTitleImage());
        assertEquals(expectedEvent.getAdditionalImages().get(0).getLink(),
            event.getAdditionalImages().get(0).getLink());

        eventToUpdateDto.setAdditionalImages(null);
        method.invoke(eventService, event, eventToUpdateDto, null);
        assertNull(event.getAdditionalImages());

        eventToUpdateDto.setTitleImage(null);
        expectedEvent.setTitleImage(AppConstant.DEFAULT_EVENT_IMAGES);
        method.invoke(eventService, event, eventToUpdateDto, null);
        assertEquals(expectedEvent.getTitleImage(), event.getTitleImage());

        MultipartFile[] multipartFiles = ModelUtils.getMultipartFiles();
        when(fileService.upload(multipartFiles[0])).thenReturn("url1");
        when(fileService.upload(multipartFiles[1])).thenReturn("url2");

        method.invoke(eventService, event, eventToUpdateDto, multipartFiles);

        expectedEvent.setTitleImage("url1");
        expectedEvent.setAdditionalImages(List.of(EventImages.builder().event(expectedEvent).link("url2").build()));

        method.invoke(eventService, event, eventToUpdateDto, multipartFiles);
        assertEquals(expectedEvent.getTitleImage(), event.getTitleImage());
        assertEquals(expectedEvent.getAdditionalImages().get(0).getLink(),
            event.getAdditionalImages().get(0).getLink());

        eventToUpdateDto.setImagesToDelete(null);
        eventToUpdateDto.setTitleImage("url");
        eventToUpdateDto.setAdditionalImages(List.of("Add img 1", "Add img 2"));
        expectedEvent.setTitleImage("url");
        expectedEvent.setAdditionalImages(List.of(EventImages.builder().event(expectedEvent).link("Add img 1").build(),
            EventImages.builder().event(expectedEvent).link("Add img 2").build()));
        method.invoke(eventService, event, eventToUpdateDto, multipartFiles);
        assertEquals(expectedEvent.getTitleImage(), event.getTitleImage());
        assertEquals(expectedEvent.getAdditionalImages().get(0).getLink(),
            event.getAdditionalImages().get(0).getLink());
        assertEquals("url2", event.getAdditionalImages().get(3).getLink());

        eventToUpdateDto.setAdditionalImages(null);
        expectedEvent.setAdditionalImages(null);
        eventToUpdateDto.setTitleImage(null);
        expectedEvent.setTitleImage("title url");
        MultipartFile multipartFile = ModelUtils.getMultipartFile();
        when(fileService.upload(multipartFile)).thenReturn("title url");

        method.invoke(eventService, event, eventToUpdateDto, new MultipartFile[] {multipartFile});
        assertEquals(expectedEvent.getTitleImage(), event.getTitleImage());
        assertNull(event.getAdditionalImages());
    }

    @Test
    void updateTitleImage() {
        EventDto eventDto = ModelUtils.getEventDto();
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        Event event = ModelUtils.getEvent();
        List<Long> eventIds = List.of(event.getId());
        User user = ModelUtils.getUser();

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(restClient.findByEmail(anyString())).thenReturn(TEST_USER_VO);
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of(event));
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of(event));

        EventDto updatedEventDto = eventService.update(eventToUpdateDto, user.getEmail(), null);
        assertEquals(updatedEventDto, eventDto);
        assertTrue(updatedEventDto.isFavorite());
        assertTrue(updatedEventDto.isSubscribed());

        eventToUpdateDto.setTitle("New title");
        eventToUpdateDto.setDescription("New description");
        eventToUpdateDto.setIsOpen(false);
        eventToUpdateDto.setTags(ModelUtils.getUpdatedEventTags());
        eventToUpdateDto.setDatesLocations(ModelUtils.getUpdatedEventDateLocationDto());

        eventDto.setTitle("New title");
        eventDto.setDescription("New description");
        eventDto.setOpen(false);
        eventDto.setTags(ModelUtils.getUpdatedEventTagUaEn());
        eventDto.setDates(ModelUtils.getUpdatedEventDateLocationDto());

        List<TagVO> updatedTagVO = List.of(ModelUtils.getTagVO());

        when(tagService.findTagsWithAllTranslationsByNamesAndType(eventToUpdateDto.getTags(), TagType.EVENT))
            .thenReturn(updatedTagVO);
        when(modelMapper.map(updatedTagVO, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(ModelUtils.getEventTags());
        doNothing().when(eventRepo).deleteEventDateLocationsByEventId(1L);
        when(modelMapper.map(eventToUpdateDto.getDatesLocations().get(0), EventDateLocation.class))
            .thenReturn(ModelUtils.getUpdatedEventDateLocation());
        when(googleApiService.getResultFromGeoCodeByCoordinates(any()))
            .thenReturn(ModelUtils.getAddressLatLngResponse());

        assertEquals(updatedEventDto, eventDto);
        assertTrue(updatedEventDto.isFavorite());
        assertTrue(updatedEventDto.isSubscribed());

        verify(restClient).findByEmail(anyString());
    }

    @ParameterizedTest
    @MethodSource("provideUserVOForDeleteEventTest")
    void deleteEventTest(UserVO userVO) {
        Event event = ModelUtils.getEvent();
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(restClient.findByEmail(userVO.getEmail())).thenReturn(userVO);

        eventService.delete(event.getId(), userVO.getEmail());

        verify(eventRepo).findById(1L);
        verify(restClient).findByEmail(userVO.getEmail());
        verify(eventRepo).delete(event);
    }

    private static Stream<Arguments> provideUserVOForDeleteEventTest() {
        return Stream.of(
            Arguments.of(ModelUtils.getUserVO(), ModelUtils.getUser()),
            Arguments.of(ModelUtils.getUserVO().setRole(Role.ROLE_ADMIN).setId(1L),
                ModelUtils.getUser().setRole(Role.ROLE_ADMIN).setId(1L)));
    }

    @Test
    void deleteEventThrowsNotFoundExceptionTest() {
        when(eventRepo.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.delete(1L, "test@mail.com"));
        verify(eventRepo).findById(1L);
    }

    @Test
    void deleteEventThrowsBadRequestExceptionTest() {
        UserVO userVO = ModelUtils.getUserVO();
        String userEmail = userVO.getEmail();
        userVO.setId(33L);
        Event event = ModelUtils.getEvent();

        when(restClient.findByEmail(userEmail)).thenReturn(userVO);
        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));

        Long eventId = event.getId();
        assertThrows(UserHasNoPermissionToAccessException.class, () -> eventService.delete(eventId, userEmail));

        verify(restClient).findByEmail(userEmail);
        verify(eventRepo).findById(1L);
    }

    @Test
    void getEventWithoutUser() {
        Event event = ModelUtils.getEvent();
        EventDto eventDto = ModelUtils.getEventDto();

        when(eventRepo.findById(anyLong())).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);

        EventDto actual = eventService.getEvent(1L, null);

        assertEquals(eventDto.getId(), actual.getId());
        assertEquals(eventDto.getAdditionalImages(), actual.getAdditionalImages());
        assertEquals(eventDto.getTitleImage(), actual.getTitleImage());
        assertFalse(actual.isSubscribed());
        assertFalse(actual.isFavorite());

        verify(eventRepo, never()).findFavoritesAmongEventIds(anyList(), anyLong());
        verify(eventRepo, never()).findSubscribedAmongEventIds(anyList(), anyLong());
    }

    @Test
    void getEventWithCurrentUser() {
        Event event = ModelUtils.getEvent();
        EventDto eventDto = ModelUtils.getEventDto();
        List<Long> eventIds = List.of(eventDto.getId());
        Principal principal = ModelUtils.getPrincipal();
        User user = ModelUtils.getUser();

        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(eventRepo.findById(anyLong())).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of(event));
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of(event));

        EventDto actual = eventService.getEvent(1L, principal);

        assertTrue(actual.isSubscribed());
        assertTrue(actual.isFavorite());

        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllUserOfflineEventsWithoutUserGeoPosition() {
        String eventType = "OFFLINE";
        List<Event> eventsOffline = List.of(ModelUtils.getEvent());
        List<Long> eventIds = List.of(eventsOffline.get(0).getId());
        EventDto expected = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAllByAttender(TEST_USER_VO.getId()))
            .thenReturn(new ArrayList<>(eventsOffline));
        when(modelMapper.map(eventsOffline,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(eventsOffline);
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of());

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllUserEvents(
                pageRequest, principal.getName(), "", "", eventType);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected, actual);
        assertTrue(actual.isFavorite());
        assertFalse(actual.isSubscribed());

        verify(restClient).findByEmail(principal.getName());
        verify(eventRepo).findAllByAttender(TEST_USER_VO.getId());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(eventsOffline,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllUserOfflineEventsWithUserGeoPositionIfEventFinishesToday() {
        String eventType = "OFFLINE";
        String userLatitude = "50.42929";
        String userLongitude = "30.53806";
        List<Event> eventsOffline = List.of(ModelUtils.getOfflineOnlineEventIfEventFinalDateToday());
        List<Long> eventIds = List.of(eventsOffline.get(0).getId());
        EventDto expected = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAllByAttender(TEST_USER_VO.getId()))
            .thenReturn(new ArrayList<>(eventsOffline));
        when(modelMapper.map(eventsOffline,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of());
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of());

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllUserEvents(
                pageRequest, principal.getName(), userLatitude, userLongitude, eventType);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertSame(expected, actual);
        assertFalse(actual.isFavorite());
        assertFalse(actual.isSubscribed());

        verify(restClient).findByEmail(principal.getName());
        verify(eventRepo, times(2)).findAllByAttender(TEST_USER_VO.getId());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(eventsOffline,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllUserEventsWithoutParams() {
        String eventType = "";
        List<Event> events = List.of(ModelUtils.getOnlineEvent());
        List<Long> eventIds = List.of(events.get(0).getId());
        List<EventDto> expected = List.of(ModelUtils.getEventDto());
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAllByAttender(user.getId()))
            .thenReturn(new ArrayList<>(events));
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId()))
            .thenReturn(List.of());
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllUserEvents(
                pageRequest, principal.getName(), "", "", eventType);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();

        assertEquals(expected.size(), actual.size());
        assertFalse(actual.get(0).isFavorite());
        assertTrue(actual.get(0).isSubscribed());

        verify(restClient).findByEmail(principal.getName());
        verify(eventRepo).findAllByAttender(TEST_USER_VO.getId());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllUserOnlineEvents() {
        String eventType = "ONLINE";
        List<Event> eventsOnline = List.of(ModelUtils.getOnlineEvent(),
            ModelUtils.getSecondEvent());
        List<Long> eventIds = List.of(eventsOnline.get(0).getId(), eventsOnline.get(1).getId());
        List<EventDto> expected = List.of(ModelUtils.getEventDto(),
            ModelUtils.getEventWithoutAddressDto());
        expected.get(1).setId(2L);
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAllByAttender(TEST_USER_VO.getId()))
            .thenReturn(new ArrayList<>(eventsOnline));
        when(modelMapper.map(eventsOnline,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of());
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of());

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllUserEvents(
                pageRequest, principal.getName(), "", "", eventType);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();

        assertEquals(expected.get(0), actual.get(0));
        actual.forEach(eventDto -> {
            assertFalse(eventDto.isFavorite());
            assertFalse(eventDto.isSubscribed());
        });

        verify(restClient).findByEmail(principal.getName());
        verify(eventRepo).findAllByAttender(TEST_USER_VO.getId());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(eventsOnline,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllUserOfflineEventsSortedByCoordinates() {
        String eventType = "OFFLINE";
        String userLatitude = "50.42929";
        String userLongitude = "30.53806";
        Event firstEvent = ModelUtils.getEvent();
        Event secondEvent = ModelUtils.getOfflineOnlineEventIfEventFinalDateToday();
        secondEvent.setId(2L);
        List<Event> events = List.of(firstEvent, secondEvent);
        List<Event> sortedEvents = List.of(firstEvent, secondEvent);

        List<Long> eventIds = List.of(firstEvent.getId(), secondEvent.getId());
        List<EventDto> expected = List.of(ModelUtils.getEventDto(), ModelUtils.getEventOfflineDto());
        expected.get(1).setId(2L);
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAllByAttender(TEST_USER_VO.getId()))
            .thenReturn(events);
        when(modelMapper.map(sortedEvents,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(events);
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllUserEvents(
                pageRequest, principal.getName(), userLatitude, userLongitude, eventType);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();

        assertEquals(expected, actual);
        actual.forEach(eventDto -> {
            assertTrue(eventDto.isFavorite());
            assertTrue(eventDto.isSubscribed());
        });

        verify(restClient).findByEmail(principal.getName());
        verify(eventRepo, times(2)).findAllByAttender(user.getId());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllUserOfflineEventsSortedByDates() {
        String eventType = "OFFLINE";
        Event firstEvent = ModelUtils.getCloseEvent();
        Event secondEvent = ModelUtils.getSecondEvent();
        List<Event> events = List.of(secondEvent, firstEvent);
        List<Event> sortedEvents = List.of(firstEvent, secondEvent);
        List<Long> eventIds = List.of(firstEvent.getId(), secondEvent.getId());
        List<EventDto> expected = List.of(ModelUtils.getEventOfflineDto(), ModelUtils.getSecondEventDto());
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAllByAttender(TEST_USER_VO.getId()))
            .thenReturn(events);
        when(modelMapper.map(sortedEvents,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(events);
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of());

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllUserEvents(
                pageRequest, principal.getName(), "", "", eventType);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();

        assertTrue(expected.contains(actual.get(0)));
        assertTrue(expected.contains(actual.get(1)));
        actual.forEach(eventDto -> {
            assertTrue(eventDto.isFavorite());
            assertFalse(eventDto.isSubscribed());
        });

        verify(restClient).findByEmail(principal.getName());
        verify(eventRepo).findAllByAttender(user.getId());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(sortedEvents,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllUserEventsThrowsBadRequestExceptionTest() {
        String eventType = "invalid event type";
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 2);
        String email = principal.getName();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);

        assertThrows(BadRequestException.class,
            () -> eventService.getAllUserEvents(pageRequest, email, "", "", eventType));
        verify(restClient).findByEmail(principal.getName());
    }

    @Test
    void getEventsCreatedByUser() {
        List<Event> events = List.of(ModelUtils.getEvent(), ModelUtils.getSecondEvent());
        List<Long> eventIds = List.of(events.get(0).getId());
        EventDto expected = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findEventsByOrganizer(pageRequest, user.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId()))
            .thenReturn(List.of(events.get(0)));
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId()))
            .thenReturn(List.of(events.get(0)));

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getEventsCreatedByUser(pageRequest, principal.getName());
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);
        assertEquals(expected, actual);
        assertTrue(actual.isFavorite());
        assertTrue(actual.isSubscribed());

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getRelatedToUserEvents() {
        List<Event> events = List.of(ModelUtils.getEvent(), ModelUtils.getSecondEvent());
        List<Long> eventIds = List.of(events.get(0).getId(), events.get(1).getId());
        List<EventDto> expected = List.of(ModelUtils.getEventDto(), ModelUtils.getSecondEventDto());
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, events.size());
        UserVO userVO = TEST_USER_VO;
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findRelatedEventsByUser(pageRequest, userVO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(List.of());
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(List.of());

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getRelatedToUserEvents(pageRequest, principal.getName());
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();

        assertArrayEquals(expected.toArray(), actual.toArray());
        actual.forEach(eventDto -> {
            assertFalse(eventDto.isFavorite());
            assertFalse(eventDto.isSubscribed());
        });

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getRelatedToUserEventsWithEmptyResult() {
        List<Event> events = new ArrayList<>();
        int eventSize = 0;
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 2);
        UserVO userVO = TEST_USER_VO;
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findRelatedEventsByUser(pageRequest, userVO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, eventSize));
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of());
        when(eventRepo.findFavoritesAmongEventIds(List.of(), user.getId())).thenReturn(events);
        when(eventRepo.findSubscribedAmongEventIds(List.of(), user.getId())).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getRelatedToUserEvents(pageRequest, principal.getName());
        int actual = eventDtoPageableAdvancedDto.getPage().size();

        assertEquals(eventSize, actual);

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findRelatedEventsByUser(pageRequest, user.getId());
    }

    @Test
    void addAttenderToOpenEvent() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getAttenderUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(user);
        when(eventRepo.save(event)).thenReturn(event);

        eventService.addAttender(1L, "danylo@gmail.com");

        verify(eventRepo).findById(any());
        verify(modelMapper).map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class);
        verify(eventRepo).save(event);
    }

    @Test
    void addAttenderToOpenEventIfAttenderIsOrganizerException() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(user);

        assertThrows(BadRequestException.class, () -> eventService.addAttender(1L, TestConst.EMAIL));

        verify(eventRepo).findById(any());
        verify(modelMapper).map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class);
        verify(eventRepo, never()).save(event);
    }

    @Test
    void addAttenderToOpenEventIfAttenderAlreadySubscribedException() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getAttenderUser();
        Set<User> userSet = new HashSet<>();
        userSet.add(ModelUtils.getAttenderUser());
        event.setAttenders(userSet);

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(user);

        assertThrows(BadRequestException.class, () -> eventService.addAttender(1L, "danylo@gmail.com"));

        verify(eventRepo).findById(any());
        verify(modelMapper).map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class);
        verify(eventRepo, never()).save(event);
    }

    @Test
    void addAttenderToCloseEvent() {
        Event event = ModelUtils.getCloseEvent();
        User user = ModelUtils.getAttenderUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(user);
        when(userRepo.findUserByIdAndByFriendId(2L, 1L)).thenReturn(Optional.of(user));
        when(eventRepo.save(event)).thenReturn(event);

        eventService.addAttender(1L, "danylo@gmail.com");

        verify(eventRepo).findById(any());
        verify(modelMapper).map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class);
        verify(userRepo).findUserByIdAndByFriendId(2L, 1L);
        verify(eventRepo).save(event);
    }

    @Test
    void addAttenderToCloseEventIfAttenderIsOrganizerException() {
        Event event = ModelUtils.getCloseEvent();
        User user = ModelUtils.getUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(user);

        assertThrows(BadRequestException.class, () -> eventService.addAttender(1L, TestConst.EMAIL));

        verify(eventRepo).findById(any());
        verify(modelMapper).map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class);
        verify(eventRepo, never()).save(event);
    }

    @Test
    void addAttenderToCloseEventIfAttenderAlreadySubscribedException() {
        Event event = ModelUtils.getCloseEvent();
        User user = ModelUtils.getAttenderUser();
        Set<User> userSet = new HashSet<>();
        userSet.add(ModelUtils.getAttenderUser());
        event.setAttenders(userSet);

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(user);

        assertThrows(BadRequestException.class, () -> eventService.addAttender(1L, "danylo@gmail.com"));

        verify(eventRepo).findById(any());
        verify(modelMapper).map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class);
        verify(eventRepo, never()).save(event);
    }

    @Test
    void addAttenderToCloseEventIfAttenderIsNotAFriendException() {
        Event event = ModelUtils.getCloseEvent();
        User user = ModelUtils.getAttenderUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(user);
        when(userRepo.findUserByIdAndByFriendId(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> eventService.addAttender(1L, "danylo@gmail.com"));

        verify(eventRepo).findById(any());
        verify(modelMapper).map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class);
        verify(userRepo).findUserByIdAndByFriendId(2L, 1L);
        verify(eventRepo, never()).save(event);
    }

    @Test
    void addAttenderToEventIfEventNotFoundException() {
        when(eventRepo.findById(any())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventService.addAttender(1L, "danylo@gmail.com"));
        verify(eventRepo).findById(any());
    }

    @Test
    void addToFavoritesTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(2L);

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));
        when(eventRepo.save(event)).thenReturn(event);

        eventService.addToFavorites(1L, TestConst.EMAIL);

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
        verify(eventRepo).save(event);
    }

    @Test
    void addToFavoritesThrowsExceptionWhenEventNotFoundTest() {
        when(eventRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.addToFavorites(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void addToFavoritesThrowsExceptionWhenUserNotFoundTest() {
        when(userRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.addToFavorites(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void addToFavoritesThrowsExceptionWhenUserHasAlreadyAddedEventToFavoritesTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.addToFavorites(1L, TestConst.EMAIL));

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void removeFromFavoritesTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));
        when(eventRepo.save(event)).thenReturn(event);

        eventService.removeFromFavorites(1L, TestConst.EMAIL);

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
        verify(eventRepo).save(event);
    }

    @Test
    void removeFromFavoritesThrowsExceptionWhenEventNotFoundTest() {
        when(eventRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.removeFromFavorites(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void removeFromFavoritesThrowsExceptionWhenUserNotFoundTest() {
        when(userRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.removeFromFavorites(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void removeFromFavoritesThrowsExceptionWhenEventIsNotInFavoritesTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(2L);

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.removeFromFavorites(1L, TestConst.EMAIL));

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void removeAttender() {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet<>();
        User user = ModelUtils.getUser();
        user.setId(22L);
        userSet.add(user);
        event.setAttenders(userSet);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(restClient.findByEmail(user.getEmail())).thenReturn(ModelUtils.getUserVO());

        eventService.removeAttender(event.getId(), user.getEmail());

        verify(eventRepo).save(event);
        verify(restClient).findByEmail(user.getEmail());
    }

    @Test
    void getAllWithoutUser() {
        List<Event> events = List.of(ModelUtils.getEvent());
        EventDto expected = ModelUtils.getEventDto();
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getAll(pageRequest, null);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertFalse(actual.isFavorite());
        assertFalse(actual.isSubscribed());

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo, never()).findFavoritesAmongEventIds(anyList(), anyLong());
        verify(eventRepo, never()).findSubscribedAmongEventIds(anyList(), anyLong());
    }

    @Test
    void getAllWithCurrentUser() {
        List<Event> events = List.of(ModelUtils.getEvent());
        List<Long> eventIds = List.of(events.get(0).getId());
        EventDto expected = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);
        User user = ModelUtils.getUser();

        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(events);
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getAll(pageRequest, principal);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertTrue(actual.isFavorite());
        assertTrue(actual.isSubscribed());

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllWithCurrentUser_WithNullInOrganizerEvent() {
        List<Event> events = List.of(ModelUtils.getEvent().setOrganizer(null));
        EventDto expected = ModelUtils.getEventDto().setOrganizer(null);
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getAll(pageRequest, principal);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());

        verify(eventRepo).findAllByOrderByIdDesc(any());
        verify(restClient).findByEmail(any());
        verify(modelMapper).map(TEST_USER_VO, User.class);

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
    }

    @Test
    void getAllWithCurrentUser_WithNullInIdOrganizerEvent() {
        User user = ModelUtils.getUser();
        List<Event> events = List.of(ModelUtils.getEvent().setOrganizer(user.setId(null)));
        EventDto expected = ModelUtils.getEventDto().setOrganizer(EventAuthorDto.builder()
            .name("User")
            .id(null)
            .build());
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getAll(pageRequest, principal);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());

        verify(eventRepo).findAllByOrderByIdDesc(any());
        verify(restClient).findByEmail(any());
        verify(modelMapper).map(TEST_USER_VO, User.class);

        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
    }

    @Test
    void searchEventsBy() {
        List<Event> events = Collections.singletonList(ModelUtils.getEvent());
        List<EventDto> eventDtos = Collections.singletonList(ModelUtils.getEventDto());
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<Event> page = new PageImpl<>(events, pageRequest, events.size());
        when(eventRepo.searchEventsBy(pageRequest, "query")).thenReturn(page);
        PageableAdvancedDto<EventDto> expected = new PageableAdvancedDto<>(eventDtos, eventDtos.size(), 0, 1,
            0, false, false, true, true);
        assertEquals(expected.getTotalPages(), eventService.searchEventsBy(pageRequest, "query").getTotalPages());
    }

    @Test
    void rateEvent() {
        Event event = ModelUtils.getEventWithFinishedDate();
        User user = ModelUtils.getAttenderUser();
        event.setAttenders(Set.of(user));
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(user.getEmail()), User.class)).thenReturn(user);
        doNothing().when(userService).updateEventOrganizerRating(event.getOrganizer().getId(), 2.0);
        List<Event> events = List.of(event, ModelUtils.getExpectedEvent(), ModelUtils.getEventWithGrades());
        when(eventRepo.getAllByOrganizer(event.getOrganizer())).thenReturn(events);
        eventService.rateEvent(event.getId(), user.getEmail(), 2);
        verify(eventRepo).save(event);
    }

    @Test
    void getAllEventAttenders() {
        Event event = ModelUtils.getEvent();
        Set<EventAttenderDto> eventAttenderDtos = Set.of(ModelUtils.getEventAttenderDto());
        User user = ModelUtils.getAttenderUser();
        event.setAttenders(Set.of(user));

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(user, EventAttenderDto.class)).thenReturn(ModelUtils.getEventAttenderDto());

        assertEquals(eventService.getAllEventAttenders(event.getId()), eventAttenderDtos);

        verify(modelMapper).map(user, EventAttenderDto.class);
    }

    @Test
    void findByIdTest() {
        Event event = ModelUtils.getEvent();
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(eventService.findById(1L)).thenReturn(ModelUtils.getEventVO());
        assertEquals(eventService.findById(1L), ModelUtils.getEventVO());
        verify(eventRepo, times(2)).findById(any());
    }

    @Test
    void findByIdThrowNotFoundExceptionTest() {
        when(eventRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.findById(1L));
        verify(eventRepo).findById(any());
    }

    @Test
    void getEventsWithAllFilters() {
        List<Event> events = List.of(ModelUtils.getEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();
        Principal principal = ModelUtils.getPrincipal();
        List<EventDto> expected = List.of(ModelUtils.getEventDto());
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAll()).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, principal, ModelUtils.getFilterEventDto(), null);
        long actual = eventDtoPageableAdvancedDto.getTotalElements();
        assertEquals(1, actual);
        assertEquals(expected, eventDtoPageableAdvancedDto.getPage());

        verify(eventRepo).findAll();
        verify(restClient).findByEmail(principal.getName());
        verify(modelMapper).map(TEST_USER_VO, User.class);
    }

    @Test
    void getEventsWithSomeFilters() {
        List<Event> events = List.of(ModelUtils.getSecondEvent(), ModelUtils.getCloseEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();
        Principal principal = ModelUtils.getPrincipal();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAll()).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, principal, ModelUtils.getFilterEventDtoWithSomeFilters(), null);
        long actual = eventDtoPageableAdvancedDto.getTotalElements();
        assertEquals(0, actual);

        verify(eventRepo).findAll();
        verify(restClient).findByEmail(principal.getName());
        verify(modelMapper).map(TEST_USER_VO, User.class);
    }

    @Test
    void getEventsWithoutFilters() {
        List<Event> events = List.of(ModelUtils.getEvent(), ModelUtils.getSecondEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<EventDto> expected = List.of(ModelUtils.getEventDto(), ModelUtils.getSecondEventDto());

        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, null, null, null);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();
        long actualSize = eventDtoPageableAdvancedDto.getTotalElements();

        assertEquals(expected, actual);
        assertEquals(expected.size(), actualSize);

        verify(eventRepo).findAllByOrderByIdDesc(pageRequest);
        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
    }

    @Test
    void getEventsWithFilterEventDtoWithCities() {
        List<Event> events = List.of(ModelUtils.getSecondEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();
        Principal principal = ModelUtils.getPrincipal();
        List<EventDto> expected = List.of(ModelUtils.getSecondEventDto());

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAll()).thenReturn(events);
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, principal, ModelUtils.getFilterEventDtoWithCities(), null);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();
        long actualSize = eventDtoPageableAdvancedDto.getTotalElements();

        assertEquals(expected.size(), actualSize);
        assertEquals(expected, actual);

        verify(eventRepo).findAll();
        verify(restClient).findByEmail(principal.getName());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
    }

    @Test
    void getEventsWithFilterEventDtoWithTagsAndWrongTag() {
        List<Event> events = List.of(ModelUtils.getEventWithTags());
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();
        Principal principal = ModelUtils.getPrincipal();
        List<EventDto> expected = List.of(ModelUtils.getSecondEventDto());

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAll()).thenReturn(events);
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, principal, ModelUtils.getFilterEventDtoWithTags(), null);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();
        long actualSize = eventDtoPageableAdvancedDto.getTotalElements();

        assertEquals(expected.size(), actualSize);
        assertEquals(expected, actual);

        verify(eventRepo).findAll();
        verify(restClient).findByEmail(principal.getName());
        verify(modelMapper).map(TEST_USER_VO, User.class);
        verify(modelMapper).map(events, new TypeToken<List<EventDto>>() {
        }.getType());
    }

    @Test
    void getEventsWithNullEventTime() {
        List<Event> events = List.of(ModelUtils.getCloseEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        User user = ModelUtils.getUser();
        Principal principal = ModelUtils.getPrincipal();

        when(restClient.findByEmail(principal.getName())).thenReturn(TEST_USER_VO);
        when(modelMapper.map(TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.findAll()).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, principal, ModelUtils.getFilterEventDtoWithOpenStatus(), null);
        long actual = eventDtoPageableAdvancedDto.getTotalElements();
        assertEquals(0, actual);

        verify(eventRepo).findAll();
        verify(restClient).findByEmail(principal.getName());
        verify(modelMapper).map(TEST_USER_VO, User.class);
    }

    @Test
    void getEventsWithInvalidPageNumberTest() {
        List<Event> events = List.of(getCloseEvent());
        PageRequest pageRequest = PageRequest.of(100500, 2);
        FilterEventDto filterEventDto = getFilterEventDtoWithOpenStatus();
        Principal principal = getPrincipal();
        when(eventRepo.findAll()).thenReturn(events);
        assertThrows(NotFoundException.class,
            () -> eventService.getEvents(pageRequest, principal, filterEventDto, null));
        verify(eventRepo).findAll();
    }

    @Test
    void getEventsFilteredWithAnonymousUser() {
        List<Event> events = List.of(ModelUtils.getCloseEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<EventDto> expected = List.of(ModelUtils.getSecondEventDto());

        when(eventRepo.findAll()).thenReturn(events);
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, null, ModelUtils.getFilterEventDtoWithClosedStatus(), null);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();
        long actualSize = eventDtoPageableAdvancedDto.getTotalElements();

        assertEquals(expected.size(), actualSize);
        assertEquals(expected, actual);

        verify(eventRepo).findAll();
        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
    }

    @Test
    void getEventsByTitleFilteredWithAnonymousUser() {
        List<Event> events = List.of(ModelUtils.getCloseEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<EventDto> expected = List.of(ModelUtils.getSecondEventDto());

        when(eventRepo.findAllByTitleContainingIgnoreCase("title")).thenReturn(events);
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, null, ModelUtils.getFilterEventDtoWithClosedStatus(), "title");
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();
        long actualSize = eventDtoPageableAdvancedDto.getTotalElements();

        assertEquals(expected.size(), actualSize);
        assertEquals(expected, actual);

        verify(eventRepo).findAllByTitleContainingIgnoreCase("title");
        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
    }

    @Test
    void getEventsFilteredWithAnonymousUserAndClosedStatus() {
        List<Event> events = List.of(ModelUtils.getCloseEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        when(eventRepo.findAll()).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, null, ModelUtils.getFilterEventDtoWithOpenStatus(), null);
        long actual = eventDtoPageableAdvancedDto.getTotalElements();
        assertEquals(0, actual);

        verify(eventRepo).findAll();
    }

    @Test
    void getEventsWithoutFilterEventDtoAndAnonymousUser() {
        List<Event> events = List.of(ModelUtils.getCloseEvent());
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<EventDto> expected = List.of(ModelUtils.getSecondEventDto());

        when(modelMapper.map(events, new TypeToken<List<EventDto>>() {
        }.getType())).thenReturn(expected);
        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getEvents(
            pageRequest, null, null, null);
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();
        long actualSize = eventDtoPageableAdvancedDto.getTotalElements();

        assertEquals(expected.size(), actualSize);
        assertEquals(expected, actual);

        verify(eventRepo).findAllByOrderByIdDesc(pageRequest);
        verify(modelMapper).map(events, new TypeToken<List<EventDto>>() {
        }.getType());
    }

    @Test
    void getAllFavoriteEventsByUserTest() {
        User user = ModelUtils.getUser();
        Pageable pageable = PageRequest.of(0, 20);
        List<Event> events = List.of(ModelUtils.getEvent());
        EventDto expected = ModelUtils.getEventDto();
        Page<Event> eventPage = new PageImpl<>(events, pageable, events.size());
        List<Long> eventIds = List.of(expected.getId());

        when(eventRepo.findAllFavoritesByUser(anyLong(), eq(pageable))).thenReturn(eventPage);
        when(modelMapper.map(restClient.findByEmail(anyString()), User.class)).thenReturn(user);
        when(modelMapper.map(events,
            new TypeToken<List<EventDto>>() {
            }.getType())).thenReturn(List.of(expected));
        when(eventRepo.findFavoritesAmongEventIds(eventIds, user.getId())).thenReturn(events);
        when(eventRepo.findSubscribedAmongEventIds(eventIds, user.getId())).thenReturn(events);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllFavoriteEventsByUser(pageable, user.getEmail());
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);
        assertEquals(expected, actual);
        assertTrue(actual.isFavorite());
        assertTrue(actual.isSubscribed());

        verify(eventRepo).findAllFavoritesByUser(anyLong(), eq(pageable));
        verify(modelMapper).map(restClient.findByEmail(anyString()), User.class);
        verify(modelMapper).map(events,
            new TypeToken<List<EventDto>>() {
            }.getType());
        verify(eventRepo).findFavoritesAmongEventIds(eventIds, user.getId());
        verify(eventRepo).findSubscribedAmongEventIds(eventIds, user.getId());
    }

    @Test
    void getAllEventAddressesTest() {
        AddressDto expectedAddressDto = ModelUtils.getAddressDto();
        Set<AddressDto> expectedAddresses = Set.of(expectedAddressDto);
        Address address = ModelUtils.getAddress();

        when(eventRepo.findAllEventsAddresses()).thenReturn(Set.of(address));
        when(modelMapper.map(address, AddressDto.class)).thenReturn(expectedAddressDto);

        Set<AddressDto> actualAddresses = eventService.getAllEventsAddresses();

        assertEquals(expectedAddresses, actualAddresses);
        assertTrue(actualAddresses.contains(expectedAddressDto));

        verify(eventRepo).findAllEventsAddresses();
        verify(modelMapper).map(address, AddressDto.class);
    }

    @Test
    void getAmountOfOrganizedAndAttendedNewsByUserIdTest() {
        when(eventRepo.getAmountOfOrganizedAndAttendedEventsByUserId(1L)).thenReturn(100L);
        Long actual = eventService.getAmountOfOrganizedAndAttendedEventsByUserId(1L);
        assertEquals(100L, actual);
        verify(eventRepo).getAmountOfOrganizedAndAttendedEventsByUserId(anyLong());
    }

    @Test
    void addToRequestedTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(2L);

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));
        when(eventRepo.save(event)).thenReturn(event);

        eventService.addToRequested(1L, TestConst.EMAIL);

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
        verify(eventRepo).save(event);
    }

    @Test
    void addToRequestedThrowsExceptionWhenEventNotFoundTest() {
        when(eventRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.addToRequested(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void addToRequestedThrowsExceptionWhenUserNotFoundTest() {
        when(userRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.addToRequested(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void addToRequestedThrowsExceptionWhenUserHasAlreadyAddedEventToRequestedTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.addToRequested(1L, TestConst.EMAIL));

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void removeFromRequestedTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));
        when(eventRepo.save(event)).thenReturn(event);

        eventService.removeFromRequested(1L, TestConst.EMAIL);

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
        verify(eventRepo).save(event);
    }

    @Test
    void removeFromRequestedThrowsExceptionWhenEventNotFoundTest() {
        when(eventRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.removeFromRequested(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void removeFromRequestedThrowsExceptionWhenUserNotFoundTest() {
        when(userRepo.findById(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> eventService.removeFromRequested(1L, TestConst.EMAIL));
        verify(eventRepo).findById(any());
    }

    @Test
    void removeFromRequestedThrowsExceptionWhenEventIsNotInRequestedTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(2L);

        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findByEmail(TestConst.EMAIL)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> eventService.removeFromRequested(1L, TestConst.EMAIL));

        verify(eventRepo).findById(any());
        verify(userRepo).findByEmail(TestConst.EMAIL);
    }

    @Test
    void getRequestedUsersTest() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        Pageable pageable = PageRequest.of(0, 20);

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findUsersByRequestedEvents(any(), any(Pageable.class)))
            .thenReturn(userPage);

        assertEquals(1, eventService.getRequestedUsers(1L, "", pageable).getTotalElements());

        verify(userRepo).findByEmail(anyString());
        verify(eventRepo).findById(any());
        verify(userRepo).findUsersByRequestedEvents(any(), any(Pageable.class));
    }

    @Test
    void getRequestedUsersThrowsUserHasNoPermissionExceptionTest() {
        Event event = ModelUtils.getEvent();
        User user = User.builder().id(20L).build();
        Pageable pageable = PageRequest.of(0, 20);

        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));

        assertThrows(UserHasNoPermissionToAccessException.class,
            () -> eventService.getRequestedUsers(1L, "", pageable));

        verify(userRepo).findByEmail(anyString());
        verify(eventRepo).findById(any());
    }

    @Test
    void getRequestedUsersThrowsUserNotFoundExceptionTest() {
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getRequestedUsers(1L, "", null));
    }

    @Test
    void approveRequest() {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getUser();
        User userToJoin = User.builder().build();
        Event event = ModelUtils.getEvent();
        event.getRequesters().add(userToJoin);

        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findById(any()))
            .thenReturn(Optional.of(userToJoin));
        when(eventRepo.save(any())).thenReturn(null);

        eventService.approveRequest(event.getId(), userVO.getEmail(), userToJoin.getId());

        verify(restClient).findByEmail(anyString());
        verify(modelMapper).map(userVO, User.class);
        verify(eventRepo).findById(any());
        verify(userRepo).findById(any());
        verify(eventRepo).save(any());
    }

    @Test
    void approveRequestUserHasNoAccess() {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getTestUser();
        Event event = ModelUtils.getEvent();

        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));

        assertThrows(UserHasNoPermissionToAccessException.class,
            () -> eventService.approveRequest(event.getId(), userVO.getEmail(), null));

        verify(restClient).findByEmail(anyString());
        verify(modelMapper).map(userVO, User.class);
        verify(eventRepo).findById(any());
    }

    @Test
    void approveRequestUserDidNotRequest() {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getUser();
        User userToJoin = User.builder().build();
        Event event = ModelUtils.getEvent();
        event.getRequesters().remove(userToJoin);

        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));

        assertThrows(BadRequestException.class,
            () -> eventService.approveRequest(event.getId(), userVO.getEmail(), userToJoin.getId()));

        verify(restClient).findByEmail(anyString());
        verify(modelMapper).map(userVO, User.class);
        verify(eventRepo).findById(any());
    }

    @Test
    void declineRequest() {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getUser();
        User userToJoin = User.builder().build();
        Event event = ModelUtils.getEvent();
        event.getRequesters().add(userToJoin);

        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(userRepo.findById(any()))
            .thenReturn(Optional.of(userToJoin));
        when(eventRepo.save(any())).thenReturn(null);

        eventService.declineRequest(event.getId(), userVO.getEmail(), userToJoin.getId());

        verify(restClient).findByEmail(anyString());
        verify(modelMapper).map(userVO, User.class);
        verify(eventRepo).findById(any());
        verify(userRepo).findById(any());
        verify(eventRepo).save(any());
    }

    @Test
    void declineRequestUserHasNoAccess() {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getTestUser();
        Event event = ModelUtils.getEvent();

        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));

        assertThrows(UserHasNoPermissionToAccessException.class,
            () -> eventService.declineRequest(event.getId(), userVO.getEmail(), null));

        verify(restClient).findByEmail(anyString());
        verify(modelMapper).map(userVO, User.class);
        verify(eventRepo).findById(any());
    }

    @Test
    void declineRequestUserDidNotRequest() {
        UserVO userVO = ModelUtils.getUserVO();
        User user = ModelUtils.getUser();
        User userToJoin = User.builder().build();
        Event event = ModelUtils.getEvent();
        event.getRequesters().remove(userToJoin);

        when(restClient.findByEmail(anyString())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));

        assertThrows(BadRequestException.class,
            () -> eventService.declineRequest(event.getId(), userVO.getEmail(), userToJoin.getId()));

        verify(restClient).findByEmail(anyString());
        verify(modelMapper).map(userVO, User.class);
        verify(eventRepo).findById(any());
    }

}

package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.client.RestClient;
import greencity.constant.AppConstant;
import greencity.dto.PageableAdvancedDto;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.EventAttenderDto;
import greencity.dto.event.EventDto;
import greencity.dto.event.UpdateEventDto;
import greencity.dto.tag.TagVO;
import greencity.dto.user.UserVO;
import greencity.entity.event.Event;
import greencity.entity.Tag;
import greencity.entity.User;
import greencity.entity.event.EventDateLocation;
import greencity.entity.event.EventImages;
import greencity.enums.Role;
import greencity.enums.TagType;
import greencity.exception.exceptions.BadRequestException;
import greencity.exception.exceptions.NotFoundException;
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
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Test
    void save() {
        EventDto eventDto = ModelUtils.getEventDto();
        AddEventDtoRequest addEventDtoRequest = ModelUtils.addEventDtoRequest;
        Event event = ModelUtils.getEvent();
        List<Tag> tags = ModelUtils.getEventTags();

        when(modelMapper.map(addEventDtoRequest, Event.class)).thenReturn(event);
        when(restClient.findByEmail(anyString())).thenReturn(ModelUtils.TEST_USER_VO);
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsByNamesAndType(anyList(), eq(TagType.ECO_NEWS))).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(tags);

        when(googleApiService.getResultFromGeoCodeByCoordinates(any()))
            .thenReturn(ModelUtils.getAddressLatLngResponse());

        assertEquals(eventDto, eventService.save(addEventDtoRequest, ModelUtils.getUser().getEmail(), null));

        MultipartFile multipartFile = ModelUtils.getMultipartFile();
        when(fileService.upload(multipartFile)).thenReturn("/url1");
        assertEquals(eventDto,
            eventService.save(addEventDtoRequest, ModelUtils.getUser().getEmail(),
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
        AddEventDtoRequest addEventDtoWithoutCoordinates = ModelUtils.addEventDtoWithoutAddressRequest;
        Event eventWithoutCoordinates = ModelUtils.getEventWithoutAddress();
        List<Tag> tags = ModelUtils.getEventTags();
        when(modelMapper.map(addEventDtoWithoutCoordinates, Event.class)).thenReturn(eventWithoutCoordinates);
        when(restClient.findByEmail(user.getEmail())).thenReturn(ModelUtils.TEST_USER_VO);
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(user);
        when(eventRepo.save(eventWithoutCoordinates)).thenReturn(eventWithoutCoordinates);
        when(modelMapper.map(eventWithoutCoordinates, EventDto.class)).thenReturn(eventDtoWithoutCoordinatesDto);
        List<TagVO> tagVOList = Collections.singletonList(ModelUtils.getTagVO());
        when(tagService.findTagsWithAllTranslationsByNamesAndType(addEventDtoWithoutCoordinates.getTags(),
            TagType.EVENT)).thenReturn(tagVOList);
        when(modelMapper.map(tagVOList, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(tags);

        assertEquals(eventDtoWithoutCoordinatesDto,
            eventService.save(addEventDtoWithoutCoordinates, user.getEmail(), null));
        verify(restClient, times(1)).findByEmail(user.getEmail());
        verify(eventRepo, times(1)).save(eventWithoutCoordinates);
        verify(tagService, times(1)).findTagsWithAllTranslationsByNamesAndType(addEventDtoWithoutCoordinates.getTags(),
            TagType.EVENT);
    }

    @Test
    void update() {
        EventDto eventDto = ModelUtils.getEventDto();
        Event expectedEvent = ModelUtils.getEvent();
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();

        when(eventRepo.findById(1L)).thenReturn(Optional.of(expectedEvent));
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(restClient.findByEmail(anyString())).thenReturn(ModelUtils.TEST_USER_VO);
        when(eventRepo.save(expectedEvent)).thenReturn(expectedEvent);
        when(modelMapper.map(expectedEvent, EventDto.class)).thenReturn(eventDto);
        EventDto actualEvent = eventService.update(eventToUpdateDto, ModelUtils.getUser().getEmail(), null);
        assertEquals(eventDto, actualEvent);
    }

    @Test
    void updateFinishedEvent() {
        Event actualEvent = ModelUtils.getEventWithFinishedDate();
        UpdateEventDto eventToUpdateDto = ModelUtils.getUpdateEventDto();
        String userEmail = ModelUtils.getUser().getEmail();

        when(eventRepo.findById(any())).thenReturn(Optional.of(actualEvent));
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(restClient.findByEmail(anyString())).thenReturn(ModelUtils.TEST_USER_VO);

        assertThrows(BadRequestException.class,
            () -> eventService.update(eventToUpdateDto, userEmail, null));
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

        List updatedTagVO = List.of(ModelUtils.getTagVO());
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
        expectedEvent.setTitleImage(AppConstant.DEFAULT_HABIT_IMAGE);
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

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());

        when(restClient.findByEmail(anyString())).thenReturn(ModelUtils.TEST_USER_VO);
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);

        EventDto updatedEventDto = eventService.update(eventToUpdateDto, ModelUtils.getUser().getEmail(), null);
        assertEquals(updatedEventDto, eventDto);

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

        List updatedTagVO = List.of(ModelUtils.getTagVO());

        when(tagService.findTagsWithAllTranslationsByNamesAndType(eventToUpdateDto.getTags(), TagType.EVENT))
            .thenReturn(updatedTagVO);
        when(modelMapper.map(updatedTagVO, new TypeToken<List<Tag>>() {
        }.getType())).thenReturn(ModelUtils.getEventTags());
        doNothing().when(eventRepo).deleteEventDateLocationsByEventId(1L);
        when(modelMapper.map(eventToUpdateDto.getDatesLocations().get(0), EventDateLocation.class))
            .thenReturn(ModelUtils.getUpdatedEventDateLocation());

        when(googleApiService.getResultFromGeoCodeByCoordinates(any()))
            .thenReturn(ModelUtils.getAddressLatLngResponse());

        updatedEventDto = eventService.update(eventToUpdateDto, ModelUtils.getUser().getEmail(), null);

        assertEquals(updatedEventDto, eventDto);
    }

    @ParameterizedTest
    @MethodSource("provideUserVOForDeleteEventTest")
    void delete(UserVO userVO, User user) {
        Event event = ModelUtils.getEvent();
        when(modelMapper.map(restClient.findByEmail(userVO.getEmail()), User.class))
            .thenReturn(user);
        when(eventRepo.getOne(any())).thenReturn(event);
        doNothing().when(fileService).delete(any());

        eventService.delete(event.getId(), userVO.getEmail());

        verify(eventRepo).delete(event);
    }

    private static Stream<Arguments> provideUserVOForDeleteEventTest() {
        return Stream.of(
            Arguments.of(ModelUtils.getUserVO(), ModelUtils.getUser()),
            Arguments.of(ModelUtils.getUserVO().setRole(Role.ROLE_ADMIN).setId(999L),
                ModelUtils.getUser().setRole(Role.ROLE_ADMIN).setId(999L)));
    }

    @Test
    void deleteWithException() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(2L);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);
        when(eventRepo.getOne(any())).thenReturn(event);
        String userEmail = ModelUtils.getUserVO().getEmail();
        Long eventId = event.getId();
        assertThrows(BadRequestException.class, () -> eventService.delete(eventId, userEmail));
    }

    @Test
    void getEvent() {
        Event event = ModelUtils.getEvent();
        EventDto eventDto = ModelUtils.getEventDto();
        when(eventRepo.findById(anyLong())).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        EventDto actual = eventService.getEvent(1L, null);
        assertEquals(eventDto.getId(), actual.getId());
        assertEquals(eventDto.getAdditionalImages(), actual.getAdditionalImages());
        assertEquals(eventDto.getTitleImage(), actual.getTitleImage());
        assertNull(eventDto.getIsSubscribed());
    }

    @Test
    void getEventWithCurrentUser() {
        Event event = ModelUtils.getEvent();
        EventDto eventDto = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(restClient.findByEmail(principal.getName())).thenReturn(ModelUtils.TEST_USER_VO);
        when(eventRepo.findById(anyLong())).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);
        EventDto actual = eventService.getEvent(1L, principal);
        assertEquals(false, actual.getIsSubscribed());
    }

    @Test
    void getAllUserEvents() {
        List<Event> events = List.of(ModelUtils.getEvent());
        EventDto expected = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(restClient.findByEmail(principal.getName())).thenReturn(ModelUtils.TEST_USER_VO);
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());

        when(eventRepo.findAllByAttender(pageRequest, ModelUtils.TEST_USER_VO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));

        when(modelMapper.map(events.get(0), EventDto.class)).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getAllUserEvents(pageRequest, principal.getName());
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected, actual);
    }

    @Test
    void getEventsCreatedByUser() {
        List<Event> events = List.of(ModelUtils.getEvent(), ModelUtils.getSecondEvent());
        EventDto expected = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 2);

        when(restClient.findByEmail(principal.getName())).thenReturn(ModelUtils.TEST_USER_VO);
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());

        when(eventRepo.findEventsByOrganizer(pageRequest, ModelUtils.TEST_USER_VO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));

        when(modelMapper.map(events.get(0), EventDto.class)).thenReturn(expected);
        when(modelMapper.map(events.get(1), EventDto.class)).thenReturn(ModelUtils.getSecondEventDto());

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getEventsCreatedByUser(pageRequest, principal.getName());
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);
        assertEquals(expected, actual);
    }

    @Test
    void getRelatedToUserEvents() {
        List<Event> events = List.of(ModelUtils.getEvent(), ModelUtils.getSecondEvent());
        List<EventDto> expected = List.of(ModelUtils.getEventDto(), ModelUtils.getSecondEventDto());
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, events.size());
        UserVO userVO = ModelUtils.TEST_USER_VO;
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        when(eventRepo.findRelatedEventsByUser(pageRequest, userVO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));

        for (int i = 0; i < events.size(); i++) {
            when(modelMapper.map(events.get(i), EventDto.class)).thenReturn(expected.get(i));
        }

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getRelatedToUserEvents(pageRequest, principal.getName());
        List<EventDto> actual = eventDtoPageableAdvancedDto.getPage();

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void getRelatedToUserEventsWhereOnlyOrganizer() {
        List<Event> events = List.of(ModelUtils.getEvent(), ModelUtils.getSecondEvent());
        List<EventDto> eventDtos = List.of(ModelUtils.getEventDto(), ModelUtils.getSecondEventDto());
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, events.size());
        UserVO userVO = ModelUtils.TEST_USER_VO;
        User user = ModelUtils.getUser();

        events.forEach(event -> {
            User someUser = ModelUtils.getUser();
            someUser.setId(user.getId() + 1);
            event.getAttenders().add(someUser);
        });

        when(restClient.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        when(eventRepo.findRelatedEventsByUser(pageRequest, userVO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));

        for (int i = 0; i < events.size(); i++) {
            when(modelMapper.map(events.get(i), EventDto.class)).thenReturn(eventDtos.get(i));
        }

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getRelatedToUserEvents(pageRequest, principal.getName());
        List<EventDto> result = eventDtoPageableAdvancedDto.getPage();

        result.forEach(eventDto -> assertFalse(eventDto.getIsSubscribed()));
    }

    @Test
    void getRelatedToUserEventsWhereAttender() {
        List<Event> events = List.of(ModelUtils.getEvent(), ModelUtils.getSecondEvent());
        List<EventDto> eventDtos = List.of(ModelUtils.getEventDto(), ModelUtils.getSecondEventDto());
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, events.size());
        UserVO userVO = ModelUtils.TEST_USER_VO;
        User user = ModelUtils.getUser();

        events.forEach(event -> event.getAttenders().add(user));

        when(restClient.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        when(eventRepo.findRelatedEventsByUser(pageRequest, userVO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));

        for (int i = 0; i < events.size(); i++) {
            when(modelMapper.map(events.get(i), EventDto.class)).thenReturn(eventDtos.get(i));
        }

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getRelatedToUserEvents(pageRequest, principal.getName());
        List<EventDto> result = eventDtoPageableAdvancedDto.getPage();

        result.forEach(eventDto -> assertTrue(eventDto.getIsSubscribed()));
    }

    @Test
    void getRelatedToUserEventsWithEmptyResult() {
        List<Event> events = new ArrayList<>();
        int eventSize = events.size();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, eventSize + 2);
        UserVO userVO = ModelUtils.TEST_USER_VO;
        User user = ModelUtils.getUser();

        when(restClient.findByEmail(principal.getName())).thenReturn(userVO);
        when(modelMapper.map(userVO, User.class)).thenReturn(user);

        when(eventRepo.findRelatedEventsByUser(pageRequest, userVO.getId()))
            .thenReturn(new PageImpl<>(events, pageRequest, eventSize));

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto =
            eventService.getRelatedToUserEvents(pageRequest, principal.getName());
        int actual = eventDtoPageableAdvancedDto.getPage().size();
        assertEquals(eventSize, actual);
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

        assertThrows(BadRequestException.class, () -> eventService.addAttender(event.getId(), user.getEmail()));

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
    void removeAttender() {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet<>();
        User user = ModelUtils.getUser();
        user.setId(22L);
        userSet.add(user);
        event.setAttenders(userSet);
        when(eventRepo.findById(any())).thenReturn(Optional.of(event));
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);

        eventService.removeAttender(event.getId(), user.getEmail());

        verify(eventRepo).save(event);
    }

    @Test
    void getAll() {
        List<Event> events = List.of(ModelUtils.getEvent());
        EventDto expected = ModelUtils.getEventDto();

        PageRequest pageRequest = PageRequest.of(0, 1);

        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(events.get(0), EventDto.class)).thenReturn(expected);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getAll(pageRequest, null);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    @Test
    void getAllWithCurrentUser() {
        List<Event> events = List.of(ModelUtils.getEvent());
        EventDto expected = ModelUtils.getEventDto();
        Principal principal = ModelUtils.getPrincipal();
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(eventRepo.findAllByOrderByIdDesc(pageRequest))
            .thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(events.get(0), EventDto.class)).thenReturn(expected);
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(restClient.findByEmail(principal.getName())).thenReturn(ModelUtils.TEST_USER_VO);

        PageableAdvancedDto<EventDto> eventDtoPageableAdvancedDto = eventService.getAll(pageRequest, principal);
        EventDto actual = eventDtoPageableAdvancedDto.getPage().get(0);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(false, actual.getIsSubscribed());
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
}

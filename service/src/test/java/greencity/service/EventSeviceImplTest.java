package greencity.service;

import greencity.ModelUtils;
import greencity.client.RestClient;
import greencity.dto.event.AddEventDtoRequest;
import greencity.dto.event.AddEventDtoResponse;
import greencity.dto.event.EventDto;
import greencity.entity.Event;
import greencity.entity.User;
import greencity.exception.exceptions.BadRequestException;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class EventSeviceImplTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    EventRepo eventRepo;

    @Mock
    RestClient restClient;

    @InjectMocks
    EventServiceImpl eventService;

    @Test
    void save() {
        AddEventDtoResponse addEventDtoResponse = ModelUtils.getAddEventDtoResponse();
        AddEventDtoRequest addEventDtoRequest = ModelUtils.addEventDtoRequest;
        Event event = ModelUtils.getEvent();

        when(modelMapper.map(addEventDtoRequest, Event.class)).thenReturn(event);
        when(restClient.findByEmail(anyString())).thenReturn(ModelUtils.TEST_USER_VO);
        when(modelMapper.map(ModelUtils.TEST_USER_VO, User.class)).thenReturn(ModelUtils.getUser());
        when(eventRepo.save(event)).thenReturn(event);
        when(modelMapper.map(event, AddEventDtoResponse.class)).thenReturn(addEventDtoResponse);

        assertEquals(addEventDtoResponse, eventService.save(addEventDtoRequest, ModelUtils.getUser().getEmail()));
    }

    @Test
    void delete() {
        Event event = ModelUtils.getEvent();
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(ModelUtils.getUser());
        when(eventRepo.getOne(any())).thenReturn(event);

        eventService.delete(event.getId(), ModelUtils.getUserVO().getEmail());

        verify(eventRepo).delete(event);
    }

    @Test
    void deleteWithException() {
        Event event = ModelUtils.getEvent();
        User user = ModelUtils.getUser();
        user.setId(2L);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);
        when(eventRepo.getOne(any())).thenReturn(event);

        assertThrows(BadRequestException.class,
            () -> eventService.delete(event.getId(), ModelUtils.getUserVO().getEmail()));
    }

    @Test
    void getEvent() {
        Event event = ModelUtils.getEvent();
        EventDto eventDto = ModelUtils.getEventDto();
        when(eventRepo.getOne(anyLong())).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);

        assertEquals(eventDto, eventService.getEvent(1L));
    }

    @Test
    void addAttender() {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet();
        User organizer = ModelUtils.getUser();
        User user = ModelUtils.getUser();
        user.setId(22L);
        userSet.add(user);
        event.setAttenders(userSet);
        when(eventRepo.getOne(any())).thenReturn(event);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class))
            .thenReturn(organizer);

        eventService.addAttender(event.getId(), user.getEmail());

        verify(eventRepo).save(event);
    }

    @Test
    void addAttenderThrows() {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet();
        userSet.add(ModelUtils.getUser());
        event.setAttenders(userSet);
        User user = ModelUtils.getUser();
        when(eventRepo.getOne(any())).thenReturn(event);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);

        assertThrows(BadRequestException.class, () -> eventService.addAttender(event.getId(), user.getEmail()));
    }

    @Test
    void removeAtetnder() {
        Event event = ModelUtils.getEvent();
        Set<User> userSet = new HashSet();
        User user = ModelUtils.getUser();
        user.setId(22L);
        userSet.add(user);
        event.setAttenders(userSet);
        when(eventRepo.getOne(any())).thenReturn(event);
        when(modelMapper.map(restClient.findByEmail(ModelUtils.getUserVO().getEmail()), User.class)).thenReturn(user);

        eventService.removeAttender(event.getId(), user.getEmail());

        verify(eventRepo).save(event);
    }

    @Test
    void getAll() {
        List<Event> events = List.of(ModelUtils.getEvent());
        PageRequest pageRequest = PageRequest.of(0, 1);

        when(eventRepo.getAll(pageRequest)).thenReturn(new PageImpl<>(events, pageRequest, events.size()));
        when(modelMapper.map(ModelUtils.getEvent(), EventDto.class)).thenReturn(ModelUtils.getEventDto());

        eventService.getAll(pageRequest);
    }
}

package greencity.service;

import greencity.ModelUtils;
import greencity.dto.PageableDto;
import greencity.dto.event.EventVO;
import greencity.dto.eventcomment.AddEventCommentDtoRequest;
import greencity.dto.eventcomment.AddEventCommentDtoResponse;
import greencity.dto.eventcomment.EventCommentDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.entity.event.Event;
import greencity.entity.event.EventComment;
import greencity.exception.exceptions.NotFoundException;
import greencity.repository.EventCommentRepo;
import greencity.repository.EventRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static greencity.ModelUtils.getUser;
import static greencity.ModelUtils.getUserVO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventCommentServiceImplTest {
    @Mock
    private EventCommentRepo eventCommentRepo;
    @Mock
    private EventService eventService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    EventRepo eventRepo;
    @InjectMocks
    private EventCommentServiceImpl eventCommentService;

    @Test
    void save() {
        UserVO userVO = getUserVO();
        User user = getUser();
        EventVO eventVO = ModelUtils.getEventVO();
        Event event = ModelUtils.getEvent();
        AddEventCommentDtoRequest addEventCommentDtoRequest = ModelUtils.getAddEventCommentDtoRequest();
        EventComment eventComment = ModelUtils.getEventComment();

        when(eventService.findById(anyLong())).thenReturn(eventVO);
        when(eventCommentRepo.save(any(EventComment.class))).then(AdditionalAnswers.returnsFirstArg());
        when(modelMapper.map(userVO, User.class)).thenReturn(user);
        when(modelMapper.map(eventVO, Event.class)).thenReturn(event);
        when(modelMapper.map(addEventCommentDtoRequest, EventComment.class)).thenReturn(eventComment);
        when(modelMapper.map(any(EventComment.class), eq(AddEventCommentDtoResponse.class)))
                .thenReturn(ModelUtils.getAddEventCommentDtoResponse());

       eventCommentService.save(1L, addEventCommentDtoRequest, userVO);
        verify(eventCommentRepo).save(any(EventComment.class));
    }

    @Test
    void countComments() {
        Event event = ModelUtils.getEvent();
        Long eventId = 1L;

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(eventCommentRepo.countEventCommentsByEvent(event)).thenReturn(1);

        assertEquals(1, eventCommentService.countComments(eventId));
    }

    @Test
    void countCommentsEventNotFoundException() {
        Long eventId = 1L;
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.countComments(eventId));
    }

    @Test
    void getAllActiveComments() {
        int pageNumber = 1;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        UserVO userVO = getUserVO();
        Long eventId = 1L;
        EventComment eventComment = ModelUtils.getEventComment();
        Event event = ModelUtils.getEvent();
        Page<EventComment> pages = new PageImpl<>(Collections.singletonList(eventComment), pageable, 1);
        EventCommentDto eventCommentDto = ModelUtils.getEventCommentDto();

        when(eventRepo.findById(1L)).thenReturn(Optional.of(event));
        when(eventCommentRepo.findAllByEventIdOrderByCreatedDateDesc(pageable, eventId)).thenReturn(pages);
        when(modelMapper.map(eventComment, EventCommentDto.class)).thenReturn(eventCommentDto);

        PageableDto<EventCommentDto> allComments = eventCommentService.getAllActiveComments(pageable, userVO, eventId);
        assertEquals(eventCommentDto, allComments.getPage().get(0));
        assertEquals(4, allComments.getTotalElements());
        assertEquals(1, allComments.getCurrentPage());
        assertEquals(1, allComments.getPage().size());
    }

    @Test
    void getAllActiveCommentsEventNotFoundException() {
        Long eventId = 1L;
        when(eventRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> eventCommentService.countComments(eventId));
    }
}

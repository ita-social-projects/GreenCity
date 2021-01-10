package greencity.service.impl;

import greencity.dto.ChatRoomDto;
import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import greencity.enums.ChatType;
import greencity.enums.UserStatus;
import greencity.exception.exceptions.ChatRoomNotFoundException;
import greencity.repository.ChatRoomRepo;
import greencity.service.ParticipantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceImplTest {
    @InjectMocks
    private ChatRoomServiceImpl chatRoomService;
    @Mock
    private ParticipantService participantService;
    @Mock
    private ChatRoomRepo chatRoomRepo;
    @Mock
    private ModelMapper modelMapper;
    private final String email = "test.artur@mail.com";
    Participant expectedParticipant;
    ChatRoom expected;
    ChatRoom expectedToReturn;
    ChatRoomDto expectedDto;
    List<ChatRoomDto> expectedListDto;
    List<ChatRoom> expectedList;
    List<ChatRoom> expectedListEmpty;
    Set<Participant> expectedSet;

    @BeforeEach
    void init() {
        expectedList = new ArrayList<>();
        expectedListEmpty = new ArrayList<>();
        expectedSet = new HashSet<>();
        expectedParticipant = Participant.builder()
            .id(1L)
            .name("artur")
            .email(email)
            .profilePicture(null)
            .userStatus(UserStatus.ACTIVATED)
            .build();
        expectedSet.add(expectedParticipant);
        expected = ChatRoom.builder()
            .id(1L)
            .name("test")
            .messages(new LinkedList<>())
            .type(ChatType.PRIVATE)
            .participants(new HashSet<>())
            .build();
        expectedList.add(expected);
        expectedToReturn = ChatRoom.builder()
            .name("chatName")
            .participants(expectedSet)
            .type(ChatType.PRIVATE)
            .build();
        expectedDto = ChatRoomDto.builder()
            .id(1L)
            .name("test")
            .messages(new LinkedList<>())
            .chatType(ChatType.PRIVATE)
            .participants(new HashSet<>())
            .build();
    }

    @Test
    void findAllByParticipantName() {
        when(participantService.findByEmail(email)).thenReturn(expectedParticipant);
        when(chatRoomRepo.findAllByParticipant(expectedParticipant)).thenReturn(expectedList);
        when(modelMapper.map(expectedList, new TypeToken<List<ChatRoomDto>>() {
        }.getType())).thenReturn(expectedListDto);
        List<ChatRoomDto> actual = chatRoomService.findAllByParticipantName(email);
        assertEquals(expectedListDto, actual);
    }

    @Test
    void findAllRoomsByParticipantsAndStatus() {
        when(chatRoomRepo.findByParticipantsAndStatus(expectedSet, expectedSet.size(), ChatType.GROUP))
            .thenReturn(expectedList);
        when(modelMapper.map(expectedList, new TypeToken<List<ChatRoomDto>>() {
        }.getType())).thenReturn(expectedListDto);
        List<ChatRoomDto> actual = chatRoomService.findAllRoomsByParticipantsAndStatus(expectedSet, ChatType.GROUP);
        assertEquals(expectedListDto, actual);
    }

    @Test
    void findChatRoomById() {
        when(chatRoomRepo.findById(1L)).thenReturn(Optional.of(expected)).thenThrow(ChatRoomNotFoundException.class);
        when(modelMapper.map(expected, ChatRoomDto.class)).thenReturn(expectedDto);
        ChatRoomDto actual = chatRoomService.findChatRoomById(1L);
        assertEquals(expectedDto, actual);
    }

    @Test
    void findPrivateByParticipants() {
        when(participantService.findByEmail(email)).thenReturn(expectedParticipant);
        when(participantService.findById(1L)).thenReturn(expectedParticipant);
        when(chatRoomRepo.findByParticipantsAndStatus(expectedSet, expectedSet.size(), ChatType.PRIVATE))
            .thenReturn(expectedListEmpty);
        when(chatRoomRepo.save(expectedToReturn)).thenReturn(expectedToReturn);
        when(modelMapper.map(expectedToReturn, ChatRoomDto.class)).thenReturn(expectedDto);
        ChatRoomDto actualExpectedListEmpty = chatRoomService.findPrivateByParticipants(1L, email);
        assertEquals(expectedDto, actualExpectedListEmpty);
        when(chatRoomRepo.findByParticipantsAndStatus(expectedSet, expectedSet.size(), ChatType.PRIVATE))
            .thenReturn(expectedList);
        when(modelMapper.map(expected, ChatRoomDto.class)).thenReturn(expectedDto);
        ChatRoomDto actualExpectedList = chatRoomService.findPrivateByParticipants(1L, email);
        assertEquals(expectedDto, actualExpectedList);
    }
}
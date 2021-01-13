package greencity.service;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import greencity.repository.ChatMessageRepo;
import greencity.service.impl.ChatMessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {
    @InjectMocks
    private ChatMessageServiceImpl chatMessageServiceImpl;
    @Mock
    private ChatMessageRepo chatMessageRepo;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ModelMapper modelMapper;
    ChatRoomDto expectedChatRoomDto;
    List<ChatMessage> expectedChatMessagesList;
    ChatRoom expectedChatRoom;
    List<ChatMessageDto> expectedChatMessageDtoList;
    ChatMessageDto expectedChatMessageDto;
    ChatMessage expectedChatMessage;

    @BeforeEach
    void init() {
        expectedChatMessageDto = ChatMessageDto.builder()
            .roomId(1L)
            .senderId(1L)
            .content("test")
            .build();
    }

    @Test
    void findAllMessagesByChatRoomId() {
        when(chatRoomService.findChatRoomById(1L)).thenReturn(expectedChatRoomDto);
        when(modelMapper.map(expectedChatRoomDto, ChatRoom.class)).thenReturn(expectedChatRoom);
        when(chatMessageRepo.findAllByRoom(expectedChatRoom)).thenReturn(expectedChatMessagesList);
        when(modelMapper.map(expectedChatMessagesList, new TypeToken<List<ChatMessageDto>>() {
        }.getType())).thenReturn(expectedChatMessageDtoList);
        List<ChatMessageDto> actual = chatMessageServiceImpl.findAllMessagesByChatRoomId(1L);
        assertEquals(expectedChatMessageDtoList, actual);
    }

    @Test
    void processMessage() {
        when(modelMapper.map(expectedChatMessageDto, ChatMessage.class)).thenReturn(expectedChatMessage);
        when(chatMessageRepo.save(expectedChatMessage)).thenReturn(expectedChatMessage);
        chatMessageServiceImpl.processMessage(expectedChatMessageDto);
        verify(messagingTemplate).convertAndSend("/room/" + expectedChatMessageDto.getRoomId() + "/queue/messages",
            expectedChatMessageDto);
    }
}
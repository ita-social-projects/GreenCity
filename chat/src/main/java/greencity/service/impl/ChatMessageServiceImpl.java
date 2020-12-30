package greencity.service.impl;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import greencity.repository.ChatMessageRepo;
import greencity.service.ChatMessageService;
import greencity.service.ChatRoomService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ChatRoomService}.
 */
@Service
@AllArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepo chatMessageRepo;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;

    private static final String ROOM_LINK = "/room/";
    private static final String MESSAGE_LINK = "/queue/messages";
    private static final String HEADER_DELETE = "delete";
    private static final String HEADER_UPDATE = "update";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatMessageDto> findAllMessagesByChatRoomId(Long chatRoomId) {
        ChatRoomDto chatRoom = chatRoomService.findChatRoomById(chatRoomId);
        List<ChatMessage> list = chatMessageRepo.findAllByRoom(modelMapper.map(chatRoom, ChatRoom.class));
        return modelMapper.map(list,
            new TypeToken<List<ChatMessageDto>>() {
            }.getType());
    }

    @Override
    public void processMessage(ChatMessageDto chatMessageDto) {
        ChatMessage message = modelMapper.map(chatMessageDto, ChatMessage.class);
        chatMessageRepo.save(message);
        messagingTemplate.convertAndSend(
            ROOM_LINK + chatMessageDto.getRoomId() + MESSAGE_LINK, chatMessageDto);
    }

    @Override
    public void deleteMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = modelMapper.map(chatMessageDto, ChatMessage.class);
        chatMessageRepo.delete(chatMessage);
        Map<String, Object> headers = new HashMap<>();
        headers.put(HEADER_DELETE, new Object());
        messagingTemplate.convertAndSend(
            ROOM_LINK + chatMessageDto.getRoomId() + MESSAGE_LINK, chatMessageDto, headers);
    }

    @Override
    public void updateMessage(ChatMessageDto chatMessageDto) {
        ChatMessage chatMessage = modelMapper.map(chatMessageDto, ChatMessage.class);
        chatMessageRepo.save(chatMessage);
        Map<String, Object> headers = new HashMap<>();
        headers.put(HEADER_UPDATE, new Object());
        messagingTemplate.convertAndSend(
            ROOM_LINK + chatMessageDto.getRoomId() + MESSAGE_LINK, chatMessageDto, headers);
    }

    @Override
    public ChatMessageDto findTopByOrderByIdDesc() {
        return modelMapper.map(chatMessageRepo.findTopByOrderByIdDesc(), ChatMessageDto.class);
    }
}

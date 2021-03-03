package greencity.service.impl;

import greencity.constant.ErrorMessage;
import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.dto.MessageLike;
import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import greencity.exception.exceptions.ChatRoomNotFoundException;
import greencity.repository.ChatMessageRepo;
import greencity.repository.ChatRoomRepo;
import greencity.service.ChatMessageService;
import greencity.service.ChatRoomService;

import java.util.ArrayList;
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
    private final ChatRoomRepo chatRoomRepo;

    private static final String ROOM_LINK = "/room/";
    private static final String MESSAGE_LINK = "/queue/messages";
    private static final String HEADER_DELETE = "delete";
    private static final String HEADER_UPDATE = "update";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatMessageDto> findAllMessagesByChatRoomId(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepo.findById(chatRoomId)
            .orElseThrow(() -> new ChatRoomNotFoundException(ErrorMessage.CHAT_ROOM_NOT_FOUND_BY_ID));
        List<ChatMessage> messages = chatMessageRepo.findAllByRoom(chatRoom);
        List<ChatMessageDto> chatMessageDtos = mapListChatMessageDto(messages);
        return chatMessageDtos;
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
        chatMessageDto.setLikedUserId(chatMessageRepo.getLikesByMessageId(chatMessageDto.getId()));
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

    @Override
    public void likeMessage(MessageLike messageLike) {
        if (isLiked(messageLike.getMessageId(), messageLike.getParticipantId())) {
            chatMessageRepo.deleteLikeFromMessage(messageLike.getMessageId(), messageLike.getParticipantId());
        } else {
            chatMessageRepo.addLikeToMessage(messageLike.getMessageId(), messageLike.getParticipantId());
        }
        Map<String, Object> headers = new HashMap<>();
        headers.put(HEADER_UPDATE, new Object());
        ChatMessage chatMessage = chatMessageRepo.findById(messageLike.getMessageId()).get();
        ChatMessageDto chatMessageDto = modelMapper.map(chatMessage,
            ChatMessageDto.class);
        chatMessageDto.setLikedUserId(chatMessageRepo.getLikesByMessageId(messageLike.getMessageId()));
        messagingTemplate.convertAndSend(
            ROOM_LINK + chatMessage.getRoom().getId() + MESSAGE_LINK, chatMessageDto, headers);
    }

    private boolean isLiked(Long messageId, Long userId) {
        Long id = chatMessageRepo.getParticipantIdIfLiked(messageId, userId);
        if (id != null) {
            return true;
        } else {
            return false;
        }
    }

    private List<ChatMessageDto> mapListChatMessageDto(List<ChatMessage> messages) {
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
        for (ChatMessage message : messages) {
            ChatMessageDto dto = modelMapper.map(message, ChatMessageDto.class);
            dto.setLikedUserId(chatMessageRepo.getLikesByMessageId(message.getId()));
            chatMessageDtos.add(dto);
        }
        return chatMessageDtos;
    }
}

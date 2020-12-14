package greencity.service.impl;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import greencity.repository.ChatMessageRepo;
import greencity.service.ChatMessageService;
import greencity.service.ChatRoomService;
import java.util.List;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ChatMessageDto> findAllMessagesByChatRoomId(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomService.findChatRoomById(chatRoomId);
        return modelMapper.map(chatMessageRepo.findAllByRoom(chatRoom),
            new TypeToken<List<ChatRoomDto>>() {
            }.getType());
    }

    @Override
    public ChatMessage processMessage(ChatMessage chatMessage) {
        /*var chatId = directRoomRepo.findByParticipants(chatMessage.ge)
            .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
        chatMessage.setChatId(chatId.get());*/
        // DirectMessage directMessage = modelMapper.map(directMessageVO, DirectMessage.class);
        ChatMessage saved = chatMessageRepo.save(chatMessage);
        return saved;
        /*messagingTemplate.convertAndSendToUser(String.valueOf(saved.getSender().getId()), "/queue/messages",
            new Object());*/
    }
}

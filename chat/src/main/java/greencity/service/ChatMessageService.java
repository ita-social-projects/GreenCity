package greencity.service;

import greencity.dto.ChatMessageDto;
import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import java.util.List;

public interface ChatMessageService {
    /**
     * Method to find all {@link ChatMessage}'s by {@link ChatRoom} id.
     *
     * @param chatRoomId {@link ChatMessage} id.
     * @return list of {@link ChatMessage} instances.
     */
    List<ChatMessageDto> findAllMessagesByChatRoomId(Long chatRoomId);

    /**
     * Method to process all {@link ChatMessageDto}'s that are sent from client
     * side.
     *
     * @param chatMessage {@link ChatMessageDto} chatMessage.
     */
    void processMessage(ChatMessageDto chatMessage);
}

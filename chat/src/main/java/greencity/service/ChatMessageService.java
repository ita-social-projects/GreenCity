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

    ChatMessage processMessage(ChatMessage chatMessage);
}

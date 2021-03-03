package greencity.mapping;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatMessage;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link ChatMessage} into
 * {@link ChatMessageDto}.
 */
@Component
public class ChatMessageDtoMapper extends AbstractConverter<ChatMessage, ChatMessageDto> {
    /**
     * Method convert {@link ChatMessage} to {@link ChatMessageDto}.
     *
     * @return {@link ChatRoomDto}
     */
    @Override
    protected ChatMessageDto convert(ChatMessage chatMessage) {
        return ChatMessageDto.builder()
            .id(chatMessage.getId())
            .content(chatMessage.getContent())
            .createDate(chatMessage.getCreateDate())
            .fileName(chatMessage.getFileName())
            .fileType(chatMessage.getFileType())
            .senderId(chatMessage.getSender().getId())
            .roomId(chatMessage.getRoom().getId())
            .build();
    }
}

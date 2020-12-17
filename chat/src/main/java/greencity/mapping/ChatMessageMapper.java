package greencity.mapping;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import java.time.ZonedDateTime;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link ChatRoom} into
 * {@link ChatRoomDto}.
 */
@Component
public class ChatMessageMapper extends AbstractConverter<ChatMessageDto, ChatMessage> {
    /**
     * Method convert {@link ChatMessage} to {@link ChatRoomDto}.
     *
     * @return {@link ChatRoomDto}
     */
    @Override
    protected ChatMessage convert(ChatMessageDto chatMessageDto) {
        return ChatMessage.builder()
            .content(chatMessageDto.getContent())
            .sender(
                Participant.builder()
                    .id(chatMessageDto.getSenderId()).build())
            .createDate(ZonedDateTime.now())
            .room(ChatRoom.builder()
                .id(chatMessageDto.getRoomId()).build())
            .build();
    }
}

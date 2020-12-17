package greencity.mapping;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.dto.ParticipantDto;
import greencity.entity.ChatRoom;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link ChatRoom} into
 * {@link ChatRoomDto}.
 */
@Component
public class ChatRoomDtoMapper extends AbstractConverter<ChatRoom, ChatRoomDto> {
    /**
     * Method convert {@link ChatRoom} to {@link ChatRoomDto}.
     *
     * @return {@link ChatRoomDto}
     */
    @Override
    protected ChatRoomDto convert(ChatRoom chatRoom) {
        return ChatRoomDto.builder()
            .id(chatRoom.getId())
            .chatType(chatRoom.getType())
            .messages(chatRoom.getMessages().stream().map(
                chatMessage -> ChatMessageDto.builder()
                    .content(chatMessage.getContent())
                    .senderId(chatMessage.getSender().getId())
                    .roomId(chatRoom.getId()).build())
                .collect(Collectors.toList()))
            .name(chatRoom.getName())
            .participants(chatRoom.getParticipants().stream().map(
                participant -> ParticipantDto.builder()
                    .name(participant.getName())
                    .profilePicture(participant.getProfilePicture())
                    .id(participant.getId())
                    .email(participant.getEmail()).build())
                .collect(Collectors.toSet()))
            .build();
    }
}

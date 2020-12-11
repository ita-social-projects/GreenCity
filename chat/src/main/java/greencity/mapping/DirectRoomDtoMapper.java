package greencity.mapping;

import greencity.dto.DirectRoomDto;
import greencity.dto.ParticipantDto;
import greencity.entity.DirectRoom;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link DirectRoom} into
 * {@link DirectRoomDto}.
 */
@Component
public class DirectRoomDtoMapper extends AbstractConverter<DirectRoom, DirectRoomDto> {
    /**
     * Method convert {@link DirectRoom} to {@link DirectRoomDto}.
     *
     * @return {@link DirectRoomDto}
     */
    @Override
    protected DirectRoomDto convert(DirectRoom directRoom) {
        return DirectRoomDto.builder()
            .firstParticipant(
                ParticipantDto.builder()
                    .id(directRoom.getFirstParticipant().getId())
                    .name(directRoom.getFirstParticipant().getName())
                    .email(directRoom.getFirstParticipant().getEmail())
                    .profilePicture(directRoom.getFirstParticipant().getProfilePicture())
                    .build()
            ).secondParticipant(
                ParticipantDto.builder()
                    .id(directRoom.getSecondParticipant().getId())
                    .name(directRoom.getSecondParticipant().getName())
                    .email(directRoom.getSecondParticipant().getEmail())
                    .profilePicture(directRoom.getSecondParticipant().getProfilePicture())
                    .build()
            )
            .lastMessageContent(directRoom.getLastMessageContent())
            .lastMessageDate(directRoom.getLastMessageDate())
            .build();
    }
}

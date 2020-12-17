/*
package greencity.mapping;

import greencity.dto.ChatMessageDto;
import greencity.dto.ChatRoomDto;
import greencity.dto.ParticipantDto;
import greencity.entity.ChatMessage;
import greencity.entity.ChatRoom;
import greencity.entity.Participant;
import java.time.ZonedDateTime;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

*/
/**
 * Class that used by {@link ModelMapper} to map {@link ChatRoom} into
 * {@link ChatRoomDto}.
 *//*

@Component
public class ParticipantMapper extends AbstractConverter<ParticipantDto, Participant> {
    */
/**
     * Method convert {@link ParticipantDto} to {@link Participant}.
     *
     * @return {@link Participant}
     *//*

    @Override
    protected Participant convert(ParticipantDto participantDto) {
        return Participant.builder()
            .email(participantDto.getEmail())
            .
            .build();
    }
}
*/

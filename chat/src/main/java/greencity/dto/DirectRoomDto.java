package greencity.dto;

import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class DirectRoomDto {
    private ParticipantDto firstParticipant;
    private ParticipantDto secondParticipant;
    String lastMessageContent;
    ZonedDateTime lastMessageDate;
}

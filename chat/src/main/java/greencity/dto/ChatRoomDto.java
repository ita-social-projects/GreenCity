package greencity.dto;

import greencity.enums.ChatType;
import java.util.List;
import java.util.Set;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ChatRoomDto {
    private Long id;
    private String name;
    private List<ChatMessageDto> messages;
    private ChatType chatType;
    private Set<ParticipantDto> participants;
}

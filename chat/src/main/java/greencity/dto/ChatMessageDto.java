package greencity.dto;

import java.time.ZonedDateTime;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ChatMessageDto {
    private Long chatRoomId;
    private Long senderId;
    private String content;
    private ZonedDateTime createDate;
}

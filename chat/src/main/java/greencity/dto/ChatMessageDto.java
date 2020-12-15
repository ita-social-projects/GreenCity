package greencity.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class ChatMessageDto {
    private Long roomId;
    private Long senderId;
    private String content;
}

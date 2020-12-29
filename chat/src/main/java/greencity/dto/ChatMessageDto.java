package greencity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
@ToString
public class ChatMessageDto {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String content;
    private ZonedDateTime createDate;
}

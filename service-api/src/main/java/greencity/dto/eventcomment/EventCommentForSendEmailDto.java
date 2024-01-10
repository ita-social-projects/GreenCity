package greencity.dto.eventcomment;

import greencity.dto.event.EventAuthorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventCommentForSendEmailDto {
    private Long id;

    private EventCommentAuthorDto author;

    private String text;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private EventAuthorDto organizer;

    private String email;

    private Long eventId;
}

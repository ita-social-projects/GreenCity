package greencity.dto.event;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EventDateDto {
    private Long id;

    private EventDto eventDto;

    private LocalDateTime startDate;

    private LocalDateTime finishDate;
}

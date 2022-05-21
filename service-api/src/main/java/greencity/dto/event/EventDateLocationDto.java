package greencity.dto.event;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class EventDateLocationDto {
    private Long id;

    private EventDto event;

    private LocalDateTime startDate;

    private LocalDateTime finishDate;

    private String onlineLink;

    private CoordinatesDto coordinates;
}

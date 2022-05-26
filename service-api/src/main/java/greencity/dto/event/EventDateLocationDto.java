package greencity.dto.event;

import lombok.*;

import javax.validation.constraints.NotEmpty;
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

    @NotEmpty
    private LocalDateTime startDate;

    @NotEmpty
    private LocalDateTime finishDate;

    private String onlineLink;

    private CoordinatesDto coordinates;
}

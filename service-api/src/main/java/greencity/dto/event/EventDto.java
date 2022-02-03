package greencity.dto.event;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EventDto {
    private Long id;

    private String title;

    private EventAuthorDto organizer;

    private String description;

    private ZonedDateTime dateTime;

    private CoordinatesDto coordinates;

    private List<String> images;
}

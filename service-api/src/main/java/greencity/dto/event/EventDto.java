package greencity.dto.event;

import lombok.*;
import org.springframework.lang.Nullable;

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

    @Nullable
    private String titleImage;

    @Nullable
    private List<String> images;
}

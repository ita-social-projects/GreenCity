package greencity.dto.event;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import java.util.List;

@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EventDto {
    private Long id;

    private String title;

    private EventAuthorDto organizer;

    private String description;

    @Max(7)
    private List<EventDateDto> dates;

    @Nullable
    private CoordinatesDto coordinates;

    @Nullable
    private String onlineLink;

    @Nullable
    private String titleImage;

    @Nullable
    @Max(4)
    private List<String> additionalImages;

    private boolean isOpen = true;
}

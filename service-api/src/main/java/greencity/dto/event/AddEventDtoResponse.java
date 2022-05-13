package greencity.dto.event;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class AddEventDtoResponse {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @Nullable
    private CoordinatesDto coordinates;

    @Nullable
    private String onlineLink;

    @Max(7)
    private List<EventDateDto> dates;

    @NotEmpty
    private EventAuthorDto organizer;
}

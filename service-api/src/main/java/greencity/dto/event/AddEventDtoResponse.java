package greencity.dto.event;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class AddEventDtoResponse {
    @NotEmpty
    private Long id;

    @NotEmpty
    private String description;

    @NotEmpty
    private String location;

    private List<String> images;

    @NotEmpty
    private ZonedDateTime dateTime;

    @NotEmpty
    private EventAuthorDto organizer;

}

package greencity.dto.event;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class AddEventDtoRequest {
    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotEmpty
    private CoordinatesDto coordinates;

    @NotEmpty
    private ZonedDateTime dateTime;

    private String titleImage;
}

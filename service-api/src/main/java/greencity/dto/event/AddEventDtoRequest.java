package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.util.List;

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
    @Max(7)
    private List<EventDateLocationDto> datesLocations;

    @NotEmpty
    private List<String> tags;

    @JsonProperty(value = "open")
    private boolean isOpen;
}

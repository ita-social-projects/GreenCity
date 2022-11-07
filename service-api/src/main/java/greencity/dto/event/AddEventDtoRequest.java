package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class AddEventDtoRequest {
    @NotEmpty
    @Size(min = 1, max = 70)
    private String title;

    @NotEmpty
    @Size(min = 20, max = 63206)
    private String description;

    @NotEmpty
    private List<EventDateLocationDto> datesLocations;

    @NotEmpty
    private List<String> tags;

    @JsonProperty(value = "open")
    private boolean isOpen;

    private Long id;

}

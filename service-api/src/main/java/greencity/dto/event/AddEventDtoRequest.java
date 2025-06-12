package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class AddEventDtoRequest {
    /**
     * 1-70 chars. No leading/trailing whitespace. No consecutive spaces.
     */
    @Pattern(
        regexp = "^[^\\s](?:[^ ]| (?! )){0,68}[^\\s]?$",
        message = "Title must be between 1 and 70 characters, must not be blank, "
            + "contain leading/trailing spaces, or consecutive spaces.")
    private String title;

    /**
     * 10-63206 chars. No leading/trailing whitespace. No consecutive spaces.
     */
    @Pattern(
        regexp = "^[^\\s][^\\s](?:[^ ]| (?! )){6,63202}[^\\s][^\\s]$",
        message = "Description must be between 10 and 63206 characters, must not be blank, "
            + "contain leading/trailing spaces, or consecutive spaces.")
    private String description;

    @NotEmpty
    @Valid
    private List<EventDateLocationDto> datesLocations;

    @NotEmpty
    private List<String> tags;

    @JsonProperty(value = "open")
    private boolean isOpen;
}

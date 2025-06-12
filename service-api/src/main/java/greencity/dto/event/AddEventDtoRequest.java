package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import greencity.annotations.DecodedSize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
public class AddEventDtoRequest {
    @NotBlank
    @DecodedSize(min = 1, max = 70)
    private String title;

    /**
     * 10-63206 chars.
     * No leading/trailing whitespace.
     * No consecutive spaces.
     */
    @Pattern(
        regexp = "^(?!.* {2,})(?!\\s)(?!.*\\s$).{10,63206}$",
        message = "Description must be between 10 and 63206 characters, must not be blank, "
            + "contain leading/trailing spaces, or consecutive spaces."
    )
    private String description;

    @NotEmpty
    @Valid
    private List<EventDateLocationDto> datesLocations;

    @NotEmpty
    private List<String> tags;

    @JsonProperty(value = "open")
    private boolean isOpen;
}

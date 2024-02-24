package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateEventDto {
    @NotNull
    private Long id;

    @Size(min = 1, max = 70)
    private String title;

    @Size(min = 20, max = 63206)
    private String description;

    @Max(7)
    @NotNull
    private List<EventDateLocationDto> datesLocations;

    private String titleImage;

    private List<String> additionalImages;

    private List<String> imagesToDelete;

    private List<String> tags;

    @JsonProperty(value = "open")
    private Boolean isOpen;
}

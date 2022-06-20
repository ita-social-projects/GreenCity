package greencity.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Getter
@Setter
public class UpdateEventDto {
    @NotEmpty
    private Long id;

    @Size(min = 1, max = 70)
    private String title;

    @Size(min = 20, max = 63206)
    private String description;

    private List<EventDateLocationDto> datesLocations;

    private String titleImage;

    private List<String> additionalImages;

    private List<String> imagesToDelete;

    @Max(7)
    private List<EventDateLocationDto> dates;

    private List<String> tags;

    @JsonProperty(value = "open")
    private Boolean isOpen;
}

package greencity.dto.event;

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
    private List<EventDateDto> dates;

    private String titleImage;

    private boolean isOpen;
}

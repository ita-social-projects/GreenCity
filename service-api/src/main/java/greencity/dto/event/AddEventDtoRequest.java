package greencity.dto.event;

import lombok.*;

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
    private String location;

    private List<String> images;
}

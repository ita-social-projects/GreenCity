package greencity.dto.event;

import lombok.*;
import org.springframework.lang.Nullable;

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

    @Nullable
    private CoordinatesDto coordinates;

    @Nullable
    private String onlineLink;

    @NotEmpty
    private List<EventDateDto> dates;

    private String titleImage;
}

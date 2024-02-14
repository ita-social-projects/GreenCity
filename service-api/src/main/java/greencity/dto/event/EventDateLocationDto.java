package greencity.dto.event;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import jakarta.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class EventDateLocationDto {
    private Long id;

    private EventDto event;

    @NotEmpty
    private ZonedDateTime startDate;

    @NotEmpty
    private ZonedDateTime finishDate;

    private String onlineLink;

    private AddressDto coordinates;
}

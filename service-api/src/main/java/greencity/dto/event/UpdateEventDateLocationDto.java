package greencity.dto.event;

import jakarta.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class UpdateEventDateLocationDto {
    @NotEmpty
    private ZonedDateTime startDate;

    @NotEmpty
    private ZonedDateTime finishDate;

    private String onlineLink;

    private UpdateAddressDto coordinates;
}

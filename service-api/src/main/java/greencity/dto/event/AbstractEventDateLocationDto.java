package greencity.dto.event;

import jakarta.validation.constraints.NotEmpty;
import java.time.ZonedDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public abstract class AbstractEventDateLocationDto {
    @NotEmpty
    private ZonedDateTime startDate;

    @NotEmpty
    private ZonedDateTime finishDate;

    private String onlineLink;

    public abstract <T extends UpdateAddressDto> T getCoordinates();
}

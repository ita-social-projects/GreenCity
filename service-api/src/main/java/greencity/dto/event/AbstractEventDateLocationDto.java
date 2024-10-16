package greencity.dto.event;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public abstract class AbstractEventDateLocationDto {
    @NotEmpty
    @NotNull
    private ZonedDateTime startDate;

    @NotEmpty
    @NotNull
    private ZonedDateTime finishDate;

    private String onlineLink;

    public abstract <T extends UpdateAddressDto> T getCoordinates();
}

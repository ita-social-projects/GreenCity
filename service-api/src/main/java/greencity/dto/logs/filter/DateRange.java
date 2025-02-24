package greencity.dto.logs.filter;

import greencity.exception.exceptions.BadRequestException;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record DateRange(
    @NotNull(message = "From date cannot be null") LocalDateTime from,
    @NotNull(message = "To date cannot be null") LocalDateTime to) {
    public DateRange {
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be earlier or equal to 'to' date");
        }
    }
}

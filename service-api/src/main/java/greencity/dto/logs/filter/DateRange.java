package greencity.dto.logs.filter;

import greencity.exception.exceptions.BadRequestException;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

public record DateRange(
        @NotNull(message = "From date cannot be null") Date from,
        @NotNull(message = "To date cannot be null") Date to
) {
    public DateRange {
        if (from.after(to)) {
            throw new BadRequestException("'from' date must be earlier or equal to 'to' date");
        }
    }
}

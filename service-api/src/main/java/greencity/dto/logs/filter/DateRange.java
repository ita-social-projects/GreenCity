package greencity.dto.logs.filter;

import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record DateRange(
        @NotNull(message = "From date cannot be null") Date from,
        @NotNull(message = "To date cannot be null") Date to
) {
    public DateRange {
        if (from.after(to)) {
            throw new IllegalArgumentException("'from' date must be ≤ 'to' date");
        }
    }
}
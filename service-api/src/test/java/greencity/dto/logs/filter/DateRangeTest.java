package greencity.dto.logs.filter;

import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {

    @Test
    void shouldCreateDateRangeWhenFromDateIsBeforeOrEqualToToDate() {
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        DateRange dateRange = new DateRange(from, to);

        assertNotNull(dateRange);
        assertEquals(from, dateRange.from());
        assertEquals(to, dateRange.to());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromDateIsAfterToDate() {
        LocalDateTime from = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        LocalDateTime to = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> new DateRange(from, to));

        assertEquals("'from' date must be earlier or equal to 'to' date", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFromDateIsNull() {
        LocalDateTime to = LocalDateTime.of(2025, 12, 31, 23, 59, 59);

        assertThrows(NullPointerException.class, () -> new DateRange(null, to));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenToDateIsNull() {
        LocalDateTime from = LocalDateTime.of(2025, 1, 1, 0, 0, 0);

        assertThrows(NullPointerException.class, () -> new DateRange(from, null));
    }
}
package greencity.dto.logs.filter;

import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {

    @Test
    void shouldCreateDateRangeWhenFromDateIsBeforeOrEqualToToDate() {
        Date from = new Date(2025, Calendar.JANUARY, 1);
        Date to = new Date(2025, Calendar.DECEMBER, 31);

        DateRange dateRange = new DateRange(from, to);

        assertNotNull(dateRange);
        assertEquals(from, dateRange.from());
        assertEquals(to, dateRange.to());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromDateIsAfterToDate() {
        Date from = new Date(2025, Calendar.DECEMBER, 31);
        Date to = new Date(2025, Calendar.JANUARY, 1);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            new DateRange(from, to);
        });

        assertEquals("'from' date must be earlier or equal to 'to' date", exception.getMessage());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromDateIsNull() {
        Date to = new Date(2025, Calendar.DECEMBER, 31);

        assertThrows(NullPointerException.class, () -> new DateRange(null, to));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenToDateIsNull() {
        Date from = new Date(2025, Calendar.JANUARY, 1);

        assertThrows(NullPointerException.class, () -> new DateRange(from, null));
    }
}
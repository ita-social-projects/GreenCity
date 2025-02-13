package greencity.dto.logs.filter;

import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateRangeTest {

    @Test
    void shouldCreateDateRangeWhenFromDateIsBeforeOrEqualToToDate() {
        // Arrange
        Date from = new Date(2025, 1, 1);  // January 1, 2025
        Date to = new Date(2025, 12, 31);  // December 31, 2025

        // Act
        DateRange dateRange = new DateRange(from, to);

        // Assert
        assertNotNull(dateRange);
        assertEquals(from, dateRange.from());
        assertEquals(to, dateRange.to());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromDateIsAfterToDate() {
        // Arrange
        Date from = new Date(2025, 12, 31);  // December 31, 2025
        Date to = new Date(2025, 1, 1);  // January 1, 2025

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            new DateRange(from, to);
        });

        assertEquals("'from' date must be earlier or equal to 'to' date", exception.getMessage());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromDateIsNull() {
        // Arrange
        Date from = null;
        Date to = new Date(2025, 12, 31);  // December 31, 2025

        // Act & Assert
        assertThrows(NullPointerException.class, () -> new DateRange(from, to));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenToDateIsNull() {
        // Arrange
        Date from = new Date(2025, 1, 1);  // January 1, 2025
        Date to = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> new DateRange(from, to));
    }
}
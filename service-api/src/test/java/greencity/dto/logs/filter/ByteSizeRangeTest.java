package greencity.dto.logs.filter;

import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteSizeRangeTest {

    @Test
    void shouldCreateByteSizeRangeWhenFromSizeIsLessThanOrEqualToToSize() {
        // Arrange
        long from = 10L;
        long to = 100L;

        // Act
        ByteSizeRange byteSizeRange = new ByteSizeRange(from, to);

        // Assert
        assertNotNull(byteSizeRange);
        assertEquals(from, byteSizeRange.from());
        assertEquals(to, byteSizeRange.to());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromSizeIsGreaterThanToSize() {
        // Arrange
        long from = 100L;
        long to = 10L;

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            new ByteSizeRange(from, to);
        });

        assertEquals("'from' size must be less or equal to 'to' size", exception.getMessage());
    }
}
package greencity.dto.logs.filter;

import greencity.exception.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteSizeRangeTest {

    @Test
    void shouldCreateByteSizeRangeWhenFromSizeIsLessThanOrEqualToToSizeTest() {
        long from = 10L;
        long to = 100L;

        ByteSizeRange byteSizeRange = new ByteSizeRange(from, to);

        assertNotNull(byteSizeRange);
        assertEquals(from, byteSizeRange.from());
        assertEquals(to, byteSizeRange.to());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenFromSizeIsGreaterThanToSizeTest() {
        long from = 100L;
        long to = 10L;

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            new ByteSizeRange(from, to);
        });

        assertEquals("'from' size must be less or equal to 'to' size", exception.getMessage());
    }
}
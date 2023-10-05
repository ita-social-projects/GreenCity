package greencity.enums;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatingCalculationEnumTest {

    @Test
    void testFindEnumByNameValidName() {
        assertEquals(RatingCalculationEnum.DAYS_OF_HABIT_IN_PROGRESS,
            RatingCalculationEnum.findByName("DAYS_OF_HABIT_IN_PROGRESS"));
        assertEquals(RatingCalculationEnum.CREATE_NEWS, RatingCalculationEnum.findByName("CREATE_NEWS"));
    }

    @Test
    void testFindEnumByNameInvalidName() {
        assertThrows(NotFoundException.class, () -> RatingCalculationEnum.findByName("INVALID_NAME"));
    }

    @Test
    void testErrorMessageOnNotFound() {
        NotFoundException exception =
            assertThrows(NotFoundException.class, () -> RatingCalculationEnum.findByName("INVALID_NAME"));
        assertEquals(ErrorMessage.RATING_CALCULATION_ENUM_NOT_FOUND_BY_NAME + "INVALID_NAME", exception.getMessage());
    }
}

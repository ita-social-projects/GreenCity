package greencity.enums;

import greencity.constant.ErrorMessage;
import greencity.exception.exceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RatingCalculationEnumTest {

    @Test
    void testRatingPoints() {
        assertEquals(1, RatingCalculationEnum.DAYS_OF_HABIT_IN_PROGRESS.getRatingPoints());
        assertEquals(20, RatingCalculationEnum.CREATE_NEWS.getRatingPoints());
        assertEquals(2, RatingCalculationEnum.COMMENT_OR_REPLY.getRatingPoints());
        assertEquals(10, RatingCalculationEnum.LIKE_COMMENT_OR_REPLY.getRatingPoints());
        assertEquals(20, RatingCalculationEnum.SHARE_NEWS.getRatingPoints());
        assertEquals(-1, RatingCalculationEnum.UNDO_DAYS_OF_HABIT_IN_PROGRESS.getRatingPoints());
        assertEquals(-20, RatingCalculationEnum.DELETE_NEWS.getRatingPoints());
        assertEquals(-2, RatingCalculationEnum.DELETE_COMMENT_OR_REPLY.getRatingPoints());
        assertEquals(-10, RatingCalculationEnum.UNLIKE_COMMENT_OR_REPLY.getRatingPoints());
        assertEquals(-20, RatingCalculationEnum.UNDO_SHARE_NEWS.getRatingPoints());
    }

    @Test
    void testFindEnumByNameValidName() {
        assertEquals(RatingCalculationEnum.DAYS_OF_HABIT_IN_PROGRESS,
            RatingCalculationEnum.findEnumByName("DAYS_OF_HABIT_IN_PROGRESS"));
        assertEquals(RatingCalculationEnum.CREATE_NEWS, RatingCalculationEnum.findEnumByName("CREATE_NEWS"));
    }

    @Test
    void testFindEnumByNameInvalidName() {
        assertThrows(NotFoundException.class, () -> RatingCalculationEnum.findEnumByName("INVALID_NAME"));
    }

    @Test
    void testErrorMessageOnNotFound() {
        NotFoundException exception =
            assertThrows(NotFoundException.class, () -> RatingCalculationEnum.findEnumByName("INVALID_NAME"));
        assertEquals(ErrorMessage.RATING_CALCULATION_ENUM_NOT_FOUND_BY_NAME + "INVALID_NAME", exception.getMessage());
    }
}

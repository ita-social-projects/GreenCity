package greencity.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RatingCalculationEnumTest {

    @Test
    void testEnumValueOf() {
        for (RatingCalculationEnum value : RatingCalculationEnum.values()) {
            assertEquals(value, RatingCalculationEnum.valueOf(value.name()),
                "Enum valueOf does not match for " + value.name());
        }
    }

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
}

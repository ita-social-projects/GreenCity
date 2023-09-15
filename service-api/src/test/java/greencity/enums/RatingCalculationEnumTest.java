package greencity.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatingCalculationEnumTest {

    @Test
    public void testEnumValues() {
        RatingCalculationEnum[] expected = {
            RatingCalculationEnum.DAYS_OF_HABIT_IN_PROGRESS,
            RatingCalculationEnum.CREATE_NEWS,
            RatingCalculationEnum.COMMENT_OR_REPLY,
            RatingCalculationEnum.LIKE_COMMENT_OR_REPLY,
            RatingCalculationEnum.SHARE_NEWS,
            RatingCalculationEnum.UNDO_DAYS_OF_HABIT_IN_PROGRESS,
            RatingCalculationEnum.DELETE_NEWS,
            RatingCalculationEnum.DELETE_COMMENT_OR_REPLY,
            RatingCalculationEnum.UNLIKE_COMMENT_OR_REPLY,
            RatingCalculationEnum.UNDO_SHARE_NEWS
        };

        RatingCalculationEnum[] actual = RatingCalculationEnum.values();
        assertEquals(expected.length, actual.length, "Enum length does not match expected length");

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], "Enum value does not match at index " + i);
        }
    }

    @Test
    public void testEnumValueOf() {
        for (RatingCalculationEnum value : RatingCalculationEnum.values()) {
            assertEquals(value, RatingCalculationEnum.valueOf(value.name()),
                "Enum valueOf does not match for " + value.name());
        }
    }

    @Test
    public void testRatingPoints() {
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

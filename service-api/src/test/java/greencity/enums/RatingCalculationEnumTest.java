package greencity.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RatingCalculationEnumTest {

    @Test
    public void testEnumValues() {
        assertEquals(20, RatingCalculationEnum.ACQUIRED_HABIT_14_DAYS.getRatingPoints());
        assertEquals(30, RatingCalculationEnum.ACQUIRED_HABIT_21_DAYS.getRatingPoints());
        assertEquals(40, RatingCalculationEnum.ACQUIRED_HABIT_30_PLUS_DAYS.getRatingPoints());
    }

    @Test
    public void testEnumSize() {
        assertEquals(26, RatingCalculationEnum.values().length);
    }
}

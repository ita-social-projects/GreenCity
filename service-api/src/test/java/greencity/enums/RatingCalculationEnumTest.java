package greencity.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RatingCalculationEnumTest {

    @Test
    void testEnumValues() {
        assertEquals(20, RatingCalculationEnum.CREATE_NEWS.getRatingPoints());
        assertEquals(2, RatingCalculationEnum.COMMENT_OR_REPLY.getRatingPoints());
        assertEquals(10, RatingCalculationEnum.LIKE_COMMENT_OR_REPLY.getRatingPoints());
    }

    @Test
    void testEnumSize() {
        assertEquals(10, RatingCalculationEnum.values().length);
    }
}

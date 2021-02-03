package greencity.dto.econews;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EcoNewsViewDtoTest {

    @Test
    void isEmpty() {
        EcoNewsViewDto ecoNewsViewDto = new EcoNewsViewDto();
        assertEquals(true,ecoNewsViewDto.isEmpty());
    }
}
package greencity.dto.econews;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EcoNewsViewDtoTest {

    @Test
    void isEmptyTrue() {
        EcoNewsViewDto ecoNewsViewDto = new EcoNewsViewDto("", "", "", "", "", "", "");
        assertEquals(true, ecoNewsViewDto.isEmpty());
    }

    @Test
    void isEmptyFalse() {
        EcoNewsViewDto ecoNewsViewDto = new EcoNewsViewDto("1", "title", "author", "text", "2020.12.12", "2021.02.01", "News");
        assertEquals(false, ecoNewsViewDto.isEmpty());
    }
}
package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.ModelUtils;
import greencity.dto.advice.AdviceDto;
import greencity.entity.localization.AdviceTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AdviceTranslateMapperTest {
    @InjectMocks
    private AdviceTranslateMapper adviceTranslateMapper;

    @Test
    void convertTest() {
        AdviceTranslation adviceTranslation = ModelUtils.getAdviceTranslation();

        AdviceDto expected = AdviceDto.builder()
            .id(adviceTranslation.getAdvice().getId())
            .content(adviceTranslation.getContent())
            .build();

        assertEquals(expected, adviceTranslateMapper.convert(adviceTranslation));
    }
}

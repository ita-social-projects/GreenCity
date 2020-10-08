package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.advice.AdviceDTO;
import greencity.dto.user.HabitDictionaryDto;
import greencity.entity.localization.AdviceTranslation;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        AdviceDTO expected = new AdviceDTO();
        HabitDictionaryDto habitDictionaryDto = new HabitDictionaryDto();
        habitDictionaryDto.setId(adviceTranslation.getAdvice().getHabitDictionary().getId());
        habitDictionaryDto.setImage(adviceTranslation.getAdvice().getHabitDictionary().getImage());
        expected.setId(adviceTranslation.getId());
        expected.setContent(adviceTranslation.getContent());
        expected.setHabitDictionary(habitDictionaryDto);

        assertEquals(expected, adviceTranslateMapper.convert(adviceTranslation));
    }
}

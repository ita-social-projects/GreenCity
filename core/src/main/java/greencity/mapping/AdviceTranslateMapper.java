package greencity.mapping;

import greencity.dto.advice.AdviceDTO;

import greencity.dto.user.HabitDictionaryDto;
import greencity.entity.localization.AdviceTranslation;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AdviceTranslateMapper extends AbstractConverter<AdviceTranslation, AdviceDTO> {
    @Override
    public AdviceDTO convert(AdviceTranslation entity) {
        AdviceDTO adviceDto = new AdviceDTO();
        HabitDictionaryDto habitDictionaryDto = new HabitDictionaryDto();
        habitDictionaryDto.setId(entity.getAdvice().getHabitDictionary().getId());
        habitDictionaryDto.setImage(entity.getAdvice().getHabitDictionary().getImage());
        adviceDto.setId(entity.getId());
        adviceDto.setContent(entity.getContent());
        adviceDto.setHabitDictionary(habitDictionaryDto);
        return adviceDto;
    }
}

package greencity.mapping;

import greencity.dto.advice.AdviceDTO;
import greencity.dto.user.HabitDictionaryDto;
import greencity.entity.AdviceTranslation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AdviceTranslateMapper implements MapperToDto<AdviceTranslation, AdviceDTO> {
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AdviceDTO convertToDto(AdviceTranslation entity) {
        AdviceDTO adviceDTO = new AdviceDTO();
        adviceDTO.setId(entity.getId());
        adviceDTO.setContent(entity.getContent());
        adviceDTO.setHabitDictionary(modelMapper
                .map(entity.getAdvice().getHabitDictionary(), HabitDictionaryDto.class));
        return adviceDTO;
    }
}

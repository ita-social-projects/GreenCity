package greencity.mapping;

import greencity.dto.advice.AdviceDto;
import greencity.entity.localization.AdviceTranslation;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AdviceTranslateMapper extends AbstractConverter<AdviceTranslation, AdviceDto> {
    @Override
    public AdviceDto convert(AdviceTranslation entity) {
        return AdviceDto.builder()
            .id(entity.getAdvice().getId())
            .content(entity.getContent()).build();
    }
}

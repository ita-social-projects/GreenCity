package greencity.mapping;

import greencity.dto.advice.AdviceDto;

import greencity.dto.habit.HabitDto;
import greencity.dto.habittranslation.HabitTranslationDto;
import greencity.entity.localization.AdviceTranslation;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AdviceTranslateMapper extends AbstractConverter<AdviceTranslation, AdviceDto> {
    @Override
    public AdviceDto convert(AdviceTranslation entity) {
        return AdviceDto.builder()
            .content(entity.getContent()).build();
    }
}

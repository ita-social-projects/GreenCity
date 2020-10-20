package greencity.mapping;

import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.habittranslation.HabitFactTranslationVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.HabitFact;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HabitFactVOMapper extends AbstractConverter<HabitFact, HabitFactVO> {
    @Override
    protected HabitFactVO convert(HabitFact habitFact) {
        return HabitFactVO.builder()
                .id(habitFact.getId())
                .habit(HabitVO.builder()
                        .id(habitFact.getHabit().getId())
                        .image(habitFact.getHabit().getImage())
                        .build())
                .translations(habitFact.getTranslations()
                        .stream().map(factTranslation -> HabitFactTranslationVO.builder()
                                .id(factTranslation.getId())
                                .factOfDayStatus(factTranslation.getFactOfDayStatus())
                                .content(factTranslation.getContent())
                                .language(LanguageVO.builder()
                                        .id(factTranslation.getLanguage().getId())
                                        .code(factTranslation.getLanguage().getCode())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

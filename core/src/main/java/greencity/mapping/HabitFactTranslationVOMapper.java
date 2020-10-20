package greencity.mapping;

import greencity.dto.habit.HabitVO;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.habittranslation.HabitFactTranslationVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.HabitFactTranslation;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HabitFactTranslationVOMapper extends AbstractConverter<HabitFactTranslation, HabitFactTranslationVO> {
    @Override
    protected HabitFactTranslationVO convert(HabitFactTranslation habitFactTranslation) {
        return HabitFactTranslationVO.builder()
                .id(habitFactTranslation.getId())
                .language(LanguageVO.builder()
                        .id(habitFactTranslation.getLanguage().getId())
                        .code(habitFactTranslation.getLanguage().getCode())
                        .build())
                .content(habitFactTranslation.getContent())
                .factOfDayStatus(habitFactTranslation.getFactOfDayStatus())
                .habitFact(HabitFactVO.builder()
                        .id(habitFactTranslation.getHabitFact().getId())
                        .habit(HabitVO.builder()
                                .id(habitFactTranslation.getHabitFact().getHabit().getId())
                                .image(habitFactTranslation.getHabitFact().getHabit().getImage())
                                .build())
                        .translations(habitFactTranslation.getHabitFact().getTranslations()
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
                        .build())
                .build();
    }
}

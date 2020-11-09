package greencity.mapping;

import greencity.dto.habitfact.HabitFactDtoResponse;
import greencity.dto.habitfact.HabitFactTranslationDto;
import greencity.dto.habitfact.HabitFactVO;
import greencity.dto.language.LanguageDTO;
import org.modelmapper.AbstractConverter;
import java.util.stream.Collectors;

public class HabitFactDtoResponseMapper extends AbstractConverter<HabitFactVO, HabitFactDtoResponse> {
    @Override
    protected HabitFactDtoResponse convert(HabitFactVO habitFactVO) {
        return HabitFactDtoResponse.builder()
            .id(habitFactVO.getId())
            .habit(habitFactVO.getHabit())
            .translations(habitFactVO.getTranslations().stream().map(
                habitFactTranslationVO -> HabitFactTranslationDto.builder()
                    .id(habitFactTranslationVO.getId())
                    .content(habitFactTranslationVO.getContent())
                    .factOfDayStatus(habitFactTranslationVO.getFactOfDayStatus())
                    .language(LanguageDTO.builder()
                        .id(habitFactTranslationVO.getLanguage().getId())
                        .code(habitFactTranslationVO.getLanguage().getCode())
                        .build())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}

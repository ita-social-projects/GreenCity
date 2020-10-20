package greencity.mapping;

import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.FactOfTheDay;
import org.modelmapper.AbstractConverter;

import java.util.stream.Collectors;

public class FactOfTheDayVOMapper extends AbstractConverter<FactOfTheDay, FactOfTheDayVO> {
    @Override
    protected FactOfTheDayVO convert(FactOfTheDay factOfTheDay) {
        return FactOfTheDayVO.builder()
                .id(factOfTheDay.getId())
                .name(factOfTheDay.getName())
                .createDate(factOfTheDay.getCreateDate())
                .factOfTheDayTranslations(factOfTheDay.getFactOfTheDayTranslations()
                        .stream().map(factOfTheDayTranslation -> FactOfTheDayTranslationVO.builder()
                                .id(factOfTheDayTranslation.getId())
                                .language(LanguageVO.builder()
                                        .id(factOfTheDayTranslation.getLanguage().getId())
                                        .code(factOfTheDayTranslation.getLanguage().getCode())
                                        .build())
                                .content(factOfTheDayTranslation.getContent())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

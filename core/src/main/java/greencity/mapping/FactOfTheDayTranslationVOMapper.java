package greencity.mapping;

import greencity.dto.factoftheday.FactOfTheDayTranslationVO;
import greencity.dto.factoftheday.FactOfTheDayVO;
import greencity.dto.language.LanguageVO;
import greencity.entity.FactOfTheDayTranslation;
import org.modelmapper.AbstractConverter;

public class FactOfTheDayTranslationVOMapper extends AbstractConverter<FactOfTheDayTranslation,
        FactOfTheDayTranslationVO> {
    @Override
    protected FactOfTheDayTranslationVO convert(FactOfTheDayTranslation factOfTheDayTranslation) {
        return FactOfTheDayTranslationVO.builder()
                .id(factOfTheDayTranslation.getId())
                .content(factOfTheDayTranslation.getContent())
                .language(LanguageVO.builder()
                        .id(factOfTheDayTranslation.getLanguage().getId())
                        .code(factOfTheDayTranslation.getLanguage().getCode())
                        .build())
                .factOfTheDay(FactOfTheDayVO.builder()
                        .id(factOfTheDayTranslation.getFactOfTheDay().getId())
                        .name(factOfTheDayTranslation.getFactOfTheDay().getName())
                        .createDate(factOfTheDayTranslation.getFactOfTheDay().getCreateDate())
                        .build())
                .build();
    }
}

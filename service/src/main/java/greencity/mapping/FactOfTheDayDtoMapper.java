package greencity.mapping;

import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationEmbeddedDTO;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageVO;
import greencity.entity.FactOfTheDay;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FactOfTheDayDtoMapper extends AbstractConverter<FactOfTheDay, FactOfTheDayDTO> {
    private final LanguageService languageService;

    @Override
    protected FactOfTheDayDTO convert(FactOfTheDay factOfTheDay) {
        return FactOfTheDayDTO.builder()
            .id(factOfTheDay.getId())
            .name(factOfTheDay.getName())
            .factOfTheDayTranslations(factOfTheDay.getFactOfTheDayTranslations().stream()
                .map(factOfTheDayTranslation -> {
                    Long languageId = factOfTheDayTranslation.getLanguageId();
                    LanguageVO languageVO = languageService.findLanguageById(languageId);

                    return FactOfTheDayTranslationEmbeddedDTO.builder()
                            .id(factOfTheDayTranslation.getId())
                            .content(factOfTheDayTranslation.getContent())
                            .language(LanguageDTO.builder()
                                    .id(languageVO.getId())
                                    .code(languageVO.getCode())
                                    .build())
                            .build();
                })
                .toList())
            .createDate(factOfTheDay.getCreateDate())
            .build();
    }
}

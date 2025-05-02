package greencity.mapping;

import greencity.dto.factoftheday.FactOfTheDayTranslationDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationEmbeddedPostDTO;
import greencity.dto.language.LanguageVO;
import greencity.entity.FactOfTheDay;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FactOfTheDayTranslationDTOMapper extends AbstractConverter<FactOfTheDay, FactOfTheDayTranslationDTO> {
    private final LanguageService languageService;

    @Override
    protected FactOfTheDayTranslationDTO convert(FactOfTheDay factOfTheDay) {
        return FactOfTheDayTranslationDTO.builder()
            .id(factOfTheDay.getId())
            .factOfTheDayTranslations(factOfTheDay.getFactOfTheDayTranslations().stream()
                .map(factOfTheDayTranslation -> {
                    Long languageId = factOfTheDayTranslation.getLanguageId();
                    String languageCode = languageService.findLanguageCodeById(languageId);

                    return FactOfTheDayTranslationEmbeddedPostDTO.builder()
                            .content(factOfTheDayTranslation.getContent())
                            .languageCode(languageCode)
                            .build();
                })
                .toList())
            .build();
    }
}

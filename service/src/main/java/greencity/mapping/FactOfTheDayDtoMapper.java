package greencity.mapping;

import greencity.dto.factoftheday.FactOfTheDayDTO;
import greencity.dto.factoftheday.FactOfTheDayTranslationEmbeddedDTO;
import greencity.dto.language.LanguageDTO;
import greencity.entity.FactOfTheDay;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class FactOfTheDayDtoMapper extends AbstractConverter<FactOfTheDay, FactOfTheDayDTO> {
    private final LanguageService languageService;

    @Lazy
    public FactOfTheDayDtoMapper(LanguageService languageService) {
        this.languageService = languageService;
    }

    @Override
    protected FactOfTheDayDTO convert(FactOfTheDay factOfTheDay) {
        return FactOfTheDayDTO.builder()
            .id(factOfTheDay.getId())
            .name(factOfTheDay.getName())
            .factOfTheDayTranslations(factOfTheDay.getFactOfTheDayTranslations().stream()
                .map(factOfTheDayTranslation -> {
                    String languageCode = factOfTheDayTranslation.getLanguageCode();
                    LanguageDTO languageDTO = languageService.findByCode(languageCode);

                    return FactOfTheDayTranslationEmbeddedDTO.builder()
                            .id(factOfTheDayTranslation.getId())
                            .content(factOfTheDayTranslation.getContent())
                            .language(languageDTO)
                            .build();
                })
                .toList())
            .createDate(factOfTheDay.getCreateDate())
            .build();
    }
}

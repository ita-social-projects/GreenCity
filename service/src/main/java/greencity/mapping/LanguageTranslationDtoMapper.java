package greencity.mapping;

import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import org.modelmapper.AbstractConverter;

public class LanguageTranslationDtoMapper extends AbstractConverter<HabitFactTranslation, LanguageTranslationDTO> {
    @Override
    protected LanguageTranslationDTO convert(HabitFactTranslation habitFactTranslation) {
        return LanguageTranslationDTO.builder()
            .content(habitFactTranslation.getContent())
            .language(LanguageDTO.builder()
                .id(habitFactTranslation.getLanguage().getId())
                .code(habitFactTranslation.getLanguage().getCode())
                .build())
            .build();
    }
}

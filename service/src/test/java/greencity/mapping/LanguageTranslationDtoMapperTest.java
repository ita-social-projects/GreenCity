package greencity.mapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

import greencity.ModelUtils;
import greencity.dto.language.LanguageDTO;
import greencity.dto.language.LanguageTranslationDTO;
import greencity.entity.HabitFactTranslation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class LanguageTranslationDtoMapperTest {
    @InjectMocks
    LanguageTranslationDtoMapper languageTranslationDtoMapper;

    @Test
    void convertTest() {
        HabitFactTranslation habitFactTranslation = ModelUtils.getHabitFactTranslation();

        LanguageTranslationDTO expected = LanguageTranslationDTO.builder()
            .language(LanguageDTO.builder()
                .id(habitFactTranslation.getLanguage().getId())
                .code(habitFactTranslation.getLanguage().getCode())
                .build())
            .content(habitFactTranslation.getContent())
            .build();

        assertEquals(expected, languageTranslationDtoMapper.convert(habitFactTranslation));
    }
}

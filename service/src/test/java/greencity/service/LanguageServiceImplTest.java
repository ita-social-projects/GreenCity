package greencity.service;

import greencity.ModelUtils;
import greencity.constant.AppConstant;
import greencity.dto.language.LanguageDTO;
import greencity.entity.Language;
import greencity.entity.localization.TagTranslation;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepo;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private LanguageRepo languageRepo;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private LanguageServiceImpl languageService;

    private final Language language = ModelUtils.getLanguage();

    @Test
    void getAllAdvices() {
        List<LanguageDTO> expected = Collections.emptyList();
        when(modelMapper.map(languageRepo.findAll(), new TypeToken<List<LanguageDTO>>() {
        }.getType())).thenReturn(expected);
        assertEquals(expected, languageService.getAllLanguages());
    }

    @Test
    void extractExistingLanguageCodeFromRequest() {
        String expectedLanguageCode = "ua";

        when(request.getParameter("language")).thenReturn(expectedLanguageCode);
        assertEquals(expectedLanguageCode, languageService.extractLanguageCodeFromRequest());
    }

    @Test
    void extractNotExistingLanguageCodeFromRequest() {
        when(request.getParameter("language")).thenReturn(null);
        Assertions.assertEquals(AppConstant.DEFAULT_LANGUAGE_CODE, languageService.extractLanguageCodeFromRequest());
    }

    @Test
    void findByCode() {
        LanguageDTO dto = new LanguageDTO(1L, "en");
        when(languageRepo.findByCode(language.getCode())).thenReturn(Optional.of(language));
        when(modelMapper.map(language, LanguageDTO.class)).thenReturn(dto);
        assertEquals(dto, languageService.findByCode(language.getCode()));
    }

    @Test
    void findCodeByIdFailed() {
        Assertions
            .assertThrows(LanguageNotFoundException.class,
                () -> languageService.findByCode("ua"));
    }

    @Test
    void findAllLanguageCodes() {
        List<String> code = Collections.singletonList(language.getCode());
        when(languageRepo.findAllLanguageCodes()).thenReturn(code);
        assertEquals(code, languageService.findAllLanguageCodes());
    }

    @Test
    void findByTagTranslationId() {
        TagTranslation tagTranslation = new TagTranslation(1L, "Tag name", null, null);
        LanguageDTO dto = new LanguageDTO(1L, "en");
        when(languageRepo.findByTagTranslationId(tagTranslation.getId())).thenReturn(Optional.of(language));
        when(modelMapper.map(language, LanguageDTO.class)).thenReturn(dto);
        assertEquals(dto, languageService.findByTagTranslationId(tagTranslation.getId()));
    }
}

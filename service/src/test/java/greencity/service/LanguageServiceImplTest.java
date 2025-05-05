package greencity.service;

import greencity.ModelUtils;
import greencity.client.UserRemoteClient;
import greencity.dto.language.LanguageDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {

    @Mock
    UserRemoteClient userRemoteClient;

    @InjectMocks
    LanguageServiceImpl languageService;

    @Test
    void getAllLanguages() {
        List<LanguageDTO> expectedResult = List.of(
            new LanguageDTO(1L, "code1"),
            new LanguageDTO(2L, "code2"));
        when(userRemoteClient.getAllLanguages())
            .thenReturn(expectedResult);

        List<LanguageDTO> actualResult = languageService.getAllLanguages();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findByCode() {
        String languageCode = "en";
        LanguageDTO expectedResult = ModelUtils.getLanguageDTO();
        when(userRemoteClient.findLanguageByCode(languageCode))
            .thenReturn(expectedResult);

        LanguageDTO actualResult = languageService.findByCode(languageCode);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findById() {
        Long languageId = 5L;
        LanguageDTO expectedResult = ModelUtils.getLanguageDTO();
        when(userRemoteClient.findLanguageById(languageId))
            .thenReturn(expectedResult);

        LanguageDTO actualResult = languageService.findById(languageId);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findAllLanguageCodes() {
        List<String> expectedResult = List.of("code1", "code2");
        when(userRemoteClient.findAllLanguageCodes())
            .thenReturn(expectedResult);

        List<String> actualResult = languageService.findAllLanguageCodes();

        assertEquals(expectedResult, actualResult);
    }
}

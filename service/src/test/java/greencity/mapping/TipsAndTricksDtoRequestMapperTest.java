package greencity.mapping;

import greencity.ModelUtils;
import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.entity.Language;
import greencity.entity.TextTranslation;
import greencity.entity.TipsAndTricks;
import greencity.entity.TitleTranslation;
import greencity.repository.LanguageRepo;
import greencity.service.LanguageServiceImpl;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class TipsAndTricksDtoRequestMapperTest {
    @InjectMocks
    ModelMapper modelMapper;

    @Mock
    LanguageRepo languageRepo;



    @Test
    void convert() {
        TipsAndTricksDtoRequestMapper mapper = new TipsAndTricksDtoRequestMapper(
            new LanguageServiceImpl(languageRepo, modelMapper, null));

        Language language = Language.builder()
            .code("en")
            .id(1L)
            .build();

        TipsAndTricks expected = TipsAndTricks.builder()
            .titleTranslations(Collections.singletonList(TitleTranslation.builder()
                .content("title content")
                .language(language)
                .build()))
            .textTranslations(Collections.singletonList(TextTranslation.builder()
                .content("text content for tips and tricks")
                .language(language)
                .build()))
            .creationDate(ZonedDateTime.now())
            .source("wiki")
            .build();


        when(languageRepo.findByCode("en")).thenReturn(Optional.of(language));
        TipsAndTricksDtoRequest tipsAndTricksDtoRequest = ModelUtils.getTipsAndTricksDtoRequestWithData();

        TipsAndTricks actual = mapper.convert(tipsAndTricksDtoRequest);

        expected.setCreationDate(actual.getCreationDate());
        assertEquals(expected, actual);
    }
}
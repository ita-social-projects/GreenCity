package greencity.mapping;

import greencity.dto.tipsandtricks.TipsAndTricksDtoRequest;
import greencity.entity.Language;
import greencity.entity.TextTranslation;
import greencity.entity.TipsAndTricks;

import java.time.ZonedDateTime;
import java.util.Collections;

import greencity.entity.TitleTranslation;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link TipsAndTricksDtoRequest} into
 * {@link TipsAndTricks}.
 */
@Component
@RequiredArgsConstructor
public class TipsAndTricksDtoRequestMapper extends AbstractConverter<TipsAndTricksDtoRequest, TipsAndTricks> {
    private final LanguageService languageService;
    private final ModelMapper modelMapper;

    /**
     * Method for converting {@link TipsAndTricksDtoRequest} into {@link TipsAndTricks}.
     *
     * @param tipsAndTricksDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    protected TipsAndTricks convert(TipsAndTricksDtoRequest tipsAndTricksDtoRequest) {
        Language titleTranslation = modelMapper.map(languageService.findByCode(tipsAndTricksDtoRequest
            .getTitleTranslation().getLanguageCode()), Language.class);
        Language textTranslation = modelMapper.map(languageService.findByCode(tipsAndTricksDtoRequest
            .getTextTranslation().getLanguageCode()), Language.class);
        return TipsAndTricks.builder()
                .source(tipsAndTricksDtoRequest.getSource())
                .titleTranslations(Collections.singletonList(TitleTranslation.builder()
                        .content(tipsAndTricksDtoRequest.getTitleTranslation().getContent())
                        .language(titleTranslation)
                        .build()))
                .textTranslations(Collections.singletonList(TextTranslation.builder()
                        .content(tipsAndTricksDtoRequest.getTextTranslation().getContent())
                        .language(textTranslation)
                        .build()))
                .creationDate(ZonedDateTime.now())
                .build();
    }
}

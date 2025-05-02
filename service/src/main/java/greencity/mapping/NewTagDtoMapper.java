package greencity.mapping;

import java.util.Optional;

import greencity.constant.AppConstant;
import greencity.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;
import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;
import greencity.entity.localization.TagTranslation;

@Component
@RequiredArgsConstructor
public class NewTagDtoMapper extends AbstractConverter<Tag, NewTagDto> {
    private final LanguageService languageService;

    @Override
    protected NewTagDto convert(Tag source) {
        Optional<TagTranslation> tagTranslationEn = source.getTagTranslations().stream()
            .filter(tagTranslation -> {
                Long languageId = tagTranslation.getLanguageId();
                String languageCode = languageService.findLanguageCodeById(languageId);
                return languageCode.equals(AppConstant.DEFAULT_LANGUAGE_CODE);
            })
            .findFirst();
        Optional<TagTranslation> tagTranslationUk = source.getTagTranslations().stream()
            .filter(tagTranslation -> {
                Long languageId = tagTranslation.getLanguageId();
                String languageCode = languageService.findLanguageCodeById(languageId);
                return languageCode.equals(AppConstant.LANGUAGE_CODE_UA);
            })
            .findFirst();
        String name = tagTranslationEn.map(TagTranslation::getName).orElse(null);
        String nameUk = tagTranslationUk.map(TagTranslation::getName).orElse(null);

        return NewTagDto.builder()
            .nameEn(name)
            .nameUk(nameUk)
            .id(source.getId())
            .build();
    }
}
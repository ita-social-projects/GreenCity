package greencity.mapping;

import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

import greencity.dto.tag.NewTagDto;
import greencity.entity.Tag;

@Component
public class NewTagDtoMapper extends AbstractConverter<Tag, NewTagDto> {

    @Override
    protected NewTagDto convert(Tag source) {
        String name = source.getTagTranslations().stream()
            .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals("en"))
            .findFirst().get().getName();
        String nameUa = source.getTagTranslations().stream()
            .filter(tagTranslation -> tagTranslation.getLanguage().getCode().equals("ua"))
            .findFirst().get().getName();

        return NewTagDto.builder()
            .name(name)
            .nameUa(nameUa)
            .id(source.getId())
            .build();
    }
}

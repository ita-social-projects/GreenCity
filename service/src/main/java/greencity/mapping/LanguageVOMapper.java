package greencity.mapping;

import greencity.dto.language.LanguageVO;
import greencity.entity.Language;
import org.modelmapper.AbstractConverter;
import org.springframework.stereotype.Component;

@Component
public class LanguageVOMapper extends AbstractConverter<Language, LanguageVO> {
    @Override
    protected LanguageVO convert(Language language) {
        return LanguageVO.builder()
            .id(language.getId())
            .code(language.getCode())
            .build();
    }
}

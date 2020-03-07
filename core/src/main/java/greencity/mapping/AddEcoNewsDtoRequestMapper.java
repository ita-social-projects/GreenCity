package greencity.mapping;

import greencity.constant.ErrorMessage;
import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.exception.exceptions.LanguageNotFoundException;
import greencity.repository.LanguageRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

/**
 * Class that used by {@link ModelMapper} to map {@link AddEcoNewsDtoRequest} into
 * {@link EcoNews}.
 */
@Component
public class AddEcoNewsDtoRequestMapper extends AbstractConverter<AddEcoNewsDtoRequest, EcoNews> {
    private LanguageRepository languageRepository;

    /**
     * All args constructor.
     *
     * @param languageRepository repository for getting language.
     */
    @Autowired
    public AddEcoNewsDtoRequestMapper(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    /**
     * Method for converting {@link AddEcoNewsDtoRequest} into {@link EcoNews}.
     *
     * @param addEcoNewsDtoRequest object to convert.
     * @return converted object.
     */
    @Override
    protected EcoNews convert(AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        EcoNews ecoNews = EcoNews.builder()
            .creationDate(ZonedDateTime.now())
            .imagePath(addEcoNewsDtoRequest.getImagePath())
            .build();

        ecoNews.setTranslations(addEcoNewsDtoRequest.getTranslations()
            .stream()
            .map(translation ->
                    new EcoNewsTranslation(null, languageRepository.findByCode(translation.getLanguage().getCode())
                            .orElseThrow(() -> new LanguageNotFoundException(ErrorMessage.INVALID_LANGUAGE_CODE)),
                            translation.getTitle(), translation.getText(), ecoNews))
            .collect(Collectors.toList()));

        return ecoNews;
    }
}

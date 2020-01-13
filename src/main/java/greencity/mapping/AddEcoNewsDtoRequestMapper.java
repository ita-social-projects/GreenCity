package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoRequest;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.repository.LanguageRepository;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddEcoNewsDtoRequestMapper extends AbstractConverter<AddEcoNewsDtoRequest, EcoNews> {
    private LanguageRepository languageRepository;

    /**
     * Constructor.
     *
     * @param languageRepository lol
     */
    @Autowired
    public AddEcoNewsDtoRequestMapper(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

    /**
     * Hello world.
     *
     * @param addEcoNewsDtoRequest dqw
     * @return qwd
     */
    @Override
    protected EcoNews convert(AddEcoNewsDtoRequest addEcoNewsDtoRequest) {
        EcoNews ecoNews = EcoNews.builder()
            .creationDate(ZonedDateTime.now())
            .imagePath(addEcoNewsDtoRequest.getImagePath())
            .text(addEcoNewsDtoRequest.getText())
            .build();

        ecoNews.setTranslations(addEcoNewsDtoRequest.getTranslations()
            .stream()
            .map(translation ->
                new EcoNewsTranslation(null, languageRepository.findByCode(translation.getLanguage().getCode())
                    .orElseThrow(() -> new RuntimeException()), translation.getTitle(), ecoNews))
            .collect(Collectors.toList()));

        return ecoNews;
    }
}

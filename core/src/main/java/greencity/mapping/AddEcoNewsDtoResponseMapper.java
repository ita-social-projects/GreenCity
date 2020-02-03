package greencity.mapping;

import greencity.dto.econews.AddEcoNewsDtoResponse;
import greencity.entity.EcoNews;
import greencity.entity.localization.EcoNewsTranslation;
import greencity.repository.EcoNewsTranslationRepo;
import greencity.service.LanguageService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class that used by {@link ModelMapper} to map {@link EcoNews} into
 * {@link AddEcoNewsDtoResponse}.
 */
@Component
public class AddEcoNewsDtoResponseMapper extends AbstractConverter<EcoNews, AddEcoNewsDtoResponse> {
    private LanguageService languageService;
    private EcoNewsTranslationRepo ecoNewsTranslationRepo;

    /**
     * All args constructor.
     *
     * @param languageService        service to extract language from request.
     * @param ecoNewsTranslationRepo repository for getting {@link EcoNewsTranslation}.
     */
    @Autowired
    public AddEcoNewsDtoResponseMapper(LanguageService languageService, EcoNewsTranslationRepo ecoNewsTranslationRepo) {
        this.languageService = languageService;
        this.ecoNewsTranslationRepo = ecoNewsTranslationRepo;
    }

    /**
     * Method for converting {@link EcoNews} into {@link AddEcoNewsDtoResponse}.
     *
     * @param ecoNews object to convert.
     * @return converted object.
     */
    @Override
    protected AddEcoNewsDtoResponse convert(EcoNews ecoNews) {
        EcoNewsTranslation translation = ecoNewsTranslationRepo.findByEcoNewsAndLanguageCode(ecoNews,
            languageService.extractLanguageCodeFromRequest());

        return new AddEcoNewsDtoResponse(ecoNews.getId(), translation.getTitle(), ecoNews.getText(),
            ecoNews.getCreationDate(), ecoNews.getImagePath());
    }
}
